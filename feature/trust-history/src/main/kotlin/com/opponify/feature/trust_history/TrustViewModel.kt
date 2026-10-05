package com.opponify.feature.trust_history

import androidx.lifecycle.viewModelScope
import com.opponify.common.architecture.BaseViewModel
import com.opponify.common.architecture.OperationResult
import com.opponify.model.Dispute
import com.opponify.model.DisputeResolutionDraft
import com.opponify.model.TrustAssessment
import com.opponify.common.architecture.UiLoadState
import java.util.UUID
import kotlinx.coroutines.launch

sealed interface TrustUiState {
    val assessment: TrustAssessment?
    val disputes: List<Dispute>
    val disputeDraft: DisputeResolutionDraft
    val loadState: UiLoadState

    data class Content(
        override val assessment: TrustAssessment? = null,
        override val disputes: List<Dispute> = emptyList(),
        override val disputeDraft: DisputeResolutionDraft = DisputeResolutionDraft(),
        override val loadState: UiLoadState = UiLoadState.Initial,
    ) : TrustUiState

    data class Error(
        val message: String,
        override val assessment: TrustAssessment? = null,
        override val disputes: List<Dispute> = emptyList(),
        override val disputeDraft: DisputeResolutionDraft = DisputeResolutionDraft(),
        override val loadState: UiLoadState = UiLoadState.Error(
            com.opponify.common.architecture.AppError.Unknown,
        ),
    ) : TrustUiState
}

class TrustViewModel(
    private val repository: TrustRepository,
) : BaseViewModel<TrustUiState>(TrustUiState.Content()) {

    fun load(subjectId: UUID) = viewModelScope.launch {
        updateState { it.content().copy(loadState = UiLoadState.Loading) }
        when (val result = repository.getTrustAssessment(subjectId)) {
            is OperationResult.Success -> {
                when (val disputes = repository.getDisputes(subjectId)) {
                    is OperationResult.Success -> updateState {
                        it.content().copy(
                            assessment = result.value,
                            disputes = disputes.value,
                            loadState = if (result.value.isStale || result.value.isUpdating) UiLoadState.Stale else UiLoadState.Loaded,
                        )
                    }
                    is OperationResult.Failure -> fail(disputes.error::class.simpleName ?: "Unable to load disputes")
                }
            }
            is OperationResult.Failure -> fail(result.error::class.simpleName ?: "Unable to load trust")
        }
    }

    fun updateDisputeNote(note: String) = updateState { it.content().copy(disputeDraft = DisputeResolutionDraft(note)) }

    fun resolveDispute(disputeId: UUID, idempotencyKey: String) = viewModelScope.launch {
        val draft = uiState.value.content().disputeDraft
        when (val result = repository.resolveDispute(disputeId, draft, idempotencyKey)) {
            is OperationResult.Success -> updateState { current ->
                current.content().copy(
                    disputes = current.content().disputes.map { if (it.id == disputeId) result.value else it },
                    disputeDraft = DisputeResolutionDraft(),
                )
            }
            is OperationResult.Failure -> fail(result.error::class.simpleName ?: "Unable to resolve dispute")
        }
    }

    private fun fail(message: String) = updateState { current ->
        val state = current.content()
        TrustUiState.Error(message, state.assessment, state.disputes, state.disputeDraft)
    }

    private fun TrustUiState.content(): TrustUiState.Content = when (this) {
        is TrustUiState.Content -> this
        is TrustUiState.Error -> TrustUiState.Content(assessment, disputes, disputeDraft, loadState)
    }
}
