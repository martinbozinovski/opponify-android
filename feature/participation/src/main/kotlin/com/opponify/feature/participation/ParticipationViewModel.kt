package com.opponify.feature.participation

import androidx.lifecycle.viewModelScope
import com.opponify.common.architecture.AppError
import com.opponify.common.architecture.BaseViewModel
import com.opponify.common.architecture.OperationResult
import com.opponify.common.architecture.UiLoadState
import com.opponify.model.AcceptedParticipation
import com.opponify.model.OpportunityRequest
import com.opponify.model.ParticipationDraft
import kotlinx.coroutines.launch
import java.util.UUID

sealed interface ParticipationUiState {
    val loadState: UiLoadState
    data class Content(
        val requests: List<OpportunityRequest> = emptyList(),
        val accepted: AcceptedParticipation? = null,
        val draft: ParticipationDraft = ParticipationDraft(),
        override val loadState: UiLoadState = UiLoadState.Loaded,
    ) : ParticipationUiState
    data class Error(
        val error: AppError,
        val draft: ParticipationDraft = ParticipationDraft(),
        override val loadState: UiLoadState = UiLoadState.Error(error),
    ) : ParticipationUiState
}

class ParticipationViewModel(
    private val repository: ParticipationRepository,
) : BaseViewModel<ParticipationUiState>(ParticipationUiState.Content(loadState = UiLoadState.Initial)) {

    fun loadRequests(opportunityId: UUID) {
        updateState { current ->
            when (current) {
                is ParticipationUiState.Content -> current.copy(loadState = UiLoadState.Loading)
                is ParticipationUiState.Error -> current.copy(loadState = UiLoadState.Loading)
            }
        }
        viewModelScope.launch {
            when (val result = repository.listRequests(opportunityId)) {
                is OperationResult.Success -> updateState { current ->
                    when (current) {
                        is ParticipationUiState.Content -> current.copy(requests = result.value, loadState = UiLoadState.Loaded)
                        is ParticipationUiState.Error -> ParticipationUiState.Content(requests = result.value)
                    }
                }
                is OperationResult.Failure -> updateState { current ->
                    ParticipationUiState.Error(result.error, current.draft())
                }
            }
        }
    }

    fun updateDraft(transform: (ParticipationDraft) -> ParticipationDraft) {
        updateState { current ->
            when (current) {
                is ParticipationUiState.Content -> current.copy(draft = transform(current.draft))
                is ParticipationUiState.Error -> current.copy(draft = transform(current.draft))
            }
        }
    }

    fun createRequest(opportunityId: UUID, idempotencyKey: String) {
        val current = uiState.value as? ParticipationUiState.Content ?: return
        updateState { current.copy(loadState = UiLoadState.Loading) }
        viewModelScope.launch {
            when (val result = repository.createRequest(opportunityId, current.draft, idempotencyKey)) {
                is OperationResult.Success -> updateState { state ->
                    when (state) {
                        is ParticipationUiState.Content -> state.copy(requests = state.requests + result.value, draft = ParticipationDraft(), loadState = UiLoadState.Loaded)
                        is ParticipationUiState.Error -> state
                    }
                }
                is OperationResult.Failure -> updateState { state -> ParticipationUiState.Error(result.error, state.draft()) }
            }
        }
    }

    fun withdrawRequest(requestId: UUID, idempotencyKey: String) = mutateRequest(idempotencyKey) { repository.withdrawRequest(requestId, idempotencyKey) }
    fun rejectRequest(requestId: UUID, idempotencyKey: String) = mutateRequest(idempotencyKey) { repository.rejectRequest(requestId, idempotencyKey) }

    fun acceptRequest(requestId: UUID, idempotencyKey: String) {
        val current = uiState.value as? ParticipationUiState.Content ?: return
        updateState { current.copy(loadState = UiLoadState.Loading) }
        viewModelScope.launch {
            when (val result = repository.acceptRequest(requestId, idempotencyKey)) {
                is OperationResult.Success -> updateState { state ->
                    when (state) {
                        is ParticipationUiState.Content -> state.copy(
                            requests = state.requests.map { if (it.id == requestId) it.copy(status = com.opponify.model.OpportunityRequestStatus.ACCEPTED) else it },
                            accepted = result.value,
                            loadState = UiLoadState.Loaded,
                        )
                        is ParticipationUiState.Error -> state
                    }
                }
                is OperationResult.Failure -> updateState { state -> ParticipationUiState.Error(result.error, state.draft()) }
            }
        }
    }

    private fun mutateRequest(
        idempotencyKey: String,
        operation: suspend () -> OperationResult<OpportunityRequest>,
    ) {
        val current = uiState.value as? ParticipationUiState.Content ?: return
        updateState { current.copy(loadState = UiLoadState.Loading) }
        viewModelScope.launch {
            when (val result = operation()) {
                is OperationResult.Success -> updateState { state ->
                    when (state) {
                        is ParticipationUiState.Content -> state.copy(requests = state.requests.map { request -> if (request.id == result.value.id) result.value else request }, loadState = UiLoadState.Loaded)
                        is ParticipationUiState.Error -> state
                    }
                }
                is OperationResult.Failure -> updateState { state -> ParticipationUiState.Error(result.error, state.draft()) }
            }
        }
    }

    private fun ParticipationUiState.draft(): ParticipationDraft = when (this) {
        is ParticipationUiState.Content -> draft
        is ParticipationUiState.Error -> draft
    }
}
