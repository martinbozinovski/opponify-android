package com.opponify.feature.opportunity

import androidx.lifecycle.viewModelScope
import com.opponify.common.architecture.AppError
import com.opponify.common.architecture.BaseViewModel
import com.opponify.common.architecture.OperationResult
import com.opponify.common.architecture.UiLoadState
import com.opponify.model.Opportunity
import com.opponify.model.OpportunityDraft
import kotlinx.coroutines.launch
import java.util.UUID

sealed interface OpportunityUiState {
    val draft: OpportunityDraft
    val loadState: UiLoadState

    data class Form(override val draft: OpportunityDraft = OpportunityDraft(), override val loadState: UiLoadState = UiLoadState.Initial) : OpportunityUiState
    data class Content(val opportunity: Opportunity, override val draft: OpportunityDraft, override val loadState: UiLoadState = UiLoadState.Loaded) : OpportunityUiState
    data class Error(
        val error: AppError,
        override val draft: OpportunityDraft = OpportunityDraft(),
        override val loadState: UiLoadState = UiLoadState.Error(error),
    ) : OpportunityUiState
}

class OpportunityViewModel(private val repository: OpportunityRepository) : BaseViewModel<OpportunityUiState>(OpportunityUiState.Form()) {
    fun updateDraft(transform: (OpportunityDraft) -> OpportunityDraft) = updateState { current ->
        when (current) {
            is OpportunityUiState.Form -> current.copy(draft = transform(current.draft))
            is OpportunityUiState.Content -> current.copy(draft = transform(current.draft))
            is OpportunityUiState.Error -> current
        }
    }

    fun create(idempotencyKey: String) {
        val draft = when (val current = uiState.value) {
            is OpportunityUiState.Form -> current.draft
            else -> return
        }
        updateState { OpportunityUiState.Form(draft, UiLoadState.Loading) }
        viewModelScope.launch {
            when (val result = repository.createOpportunity(draft, idempotencyKey)) {
                is OperationResult.Success -> updateState { OpportunityUiState.Content(result.value, result.value.toDraft()) }
                is OperationResult.Failure -> updateState { OpportunityUiState.Error(result.error) }
            }
        }
    }

    fun load(id: UUID) {
        updateState { OpportunityUiState.Form(loadState = UiLoadState.Loading) }
        viewModelScope.launch {
            when (val result = repository.getOpportunity(id)) {
                is OperationResult.Success -> updateState { OpportunityUiState.Content(result.value, result.value.toDraft()) }
                is OperationResult.Failure -> updateState { OpportunityUiState.Error(result.error) }
            }
        }
    }

    fun save(id: UUID, idempotencyKey: String) {
        val draft = (uiState.value as? OpportunityUiState.Content)?.draft ?: return
        updateState { OpportunityUiState.Content((uiState.value as OpportunityUiState.Content).opportunity, draft, UiLoadState.Loading) }
        viewModelScope.launch {
            when (val result = repository.updateOpportunity(id, draft, idempotencyKey)) {
                is OperationResult.Success -> updateState { OpportunityUiState.Content(result.value, result.value.toDraft()) }
                is OperationResult.Failure -> updateState { OpportunityUiState.Error(result.error) }
            }
        }
    }

    fun reopen(id: UUID, idempotencyKey: String) = mutate { repository.reopenOpportunity(id, idempotencyKey) }
    fun cancel(id: UUID, idempotencyKey: String) = mutate { repository.cancelOpportunity(id, idempotencyKey) }

    private fun mutate(operation: suspend () -> OperationResult<Opportunity>) {
        viewModelScope.launch {
            when (val result = operation()) {
                is OperationResult.Success -> updateState { OpportunityUiState.Content(result.value, result.value.toDraft()) }
                is OperationResult.Failure -> updateState { OpportunityUiState.Error(result.error) }
            }
        }
    }

    private fun Opportunity.toDraft() = OpportunityDraft(
        creatorUserId, creatorTeamId, sportId, need, town, timeType, startAt, endAt,
        level, desiredOpponentLevel, facilityId, freeFormLocation.orEmpty(), additionalInfo.orEmpty(),
        targetCapacity, minimumRequired,
    )
}
