package com.opponify.feature.game

import androidx.lifecycle.viewModelScope
import com.opponify.common.architecture.AppError
import com.opponify.common.architecture.BaseViewModel
import com.opponify.common.architecture.OperationResult
import com.opponify.common.architecture.UiLoadState
import com.opponify.model.GameChange
import com.opponify.model.GameDraft
import com.opponify.model.ScheduledGame
import com.opponify.model.TimeProposal
import com.opponify.model.TimeProposalDraft
import kotlinx.coroutines.launch
import java.util.UUID

sealed interface GameUiState {
    val game: ScheduledGame?
    val timeProposal: TimeProposal?
    val gameChange: GameChange?
    val draft: GameDraft
    val proposalDraft: TimeProposalDraft
    val loadState: UiLoadState

    data class Content(
        override val game: ScheduledGame? = null,
        override val timeProposal: TimeProposal? = null,
        override val gameChange: GameChange? = null,
        override val draft: GameDraft = GameDraft(),
        override val proposalDraft: TimeProposalDraft = TimeProposalDraft(),
        override val loadState: UiLoadState = UiLoadState.Initial,
    ) : GameUiState

    data class Error(
        val error: AppError,
        override val game: ScheduledGame? = null,
        override val timeProposal: TimeProposal? = null,
        override val gameChange: GameChange? = null,
        override val draft: GameDraft = GameDraft(),
        override val proposalDraft: TimeProposalDraft = TimeProposalDraft(),
        override val loadState: UiLoadState = UiLoadState.Error(error),
    ) : GameUiState
}

class GameViewModel(private val repository: GameRepository) : BaseViewModel<GameUiState>(GameUiState.Content()) {
    fun load(gameId: UUID) {
        updateState { it.content().copy(loadState = UiLoadState.Loading) }
        viewModelScope.launch { when (val r = repository.getGame(gameId)) {
            is OperationResult.Success -> updateState { it.content().copy(game = r.value, loadState = UiLoadState.Loaded) }
            is OperationResult.Failure -> fail(r.error)
        }}
    }
    fun updateDraft(transform: (GameDraft) -> GameDraft) = updateState { it.content().copy(draft = transform(it.draft)) }
    fun updateProposalDraft(transform: (TimeProposalDraft) -> TimeProposalDraft) = updateState { it.content().copy(proposalDraft = transform(it.proposalDraft)) }
    fun schedule(opportunityId: UUID, idempotencyKey: String) = mutate { repository.scheduleGame(opportunityId, uiState.value.draft(), idempotencyKey) }
    fun proposeTime(gameId: UUID, idempotencyKey: String) = mutateProposal { repository.proposeTime(gameId, uiState.value.proposalDraft(), idempotencyKey) }
    fun confirmTimeProposal(proposalId: UUID, idempotencyKey: String) = mutate { repository.confirmTimeProposal(proposalId, idempotencyKey) }
    fun rejectTimeProposal(proposalId: UUID, idempotencyKey: String) = mutateProposal { repository.rejectTimeProposal(proposalId, idempotencyKey) }
    fun proposeMaterialChange(gameId: UUID, idempotencyKey: String) = mutateChange { repository.proposeMaterialChange(gameId, uiState.value.proposalDraft(), idempotencyKey) }
    fun confirmMaterialChange(changeId: UUID, idempotencyKey: String) = mutate { repository.confirmMaterialChange(changeId, idempotencyKey) }
    fun rejectMaterialChange(changeId: UUID, idempotencyKey: String) = mutateChange { repository.rejectMaterialChange(changeId, idempotencyKey) }
    fun cancelGame(gameId: UUID, reason: String?, idempotencyKey: String) = mutate { repository.cancelGame(gameId, reason, idempotencyKey) }

    private fun mutate(op: suspend () -> OperationResult<ScheduledGame>) {
        updateState { it.content().copy(loadState = UiLoadState.Loading) }
        viewModelScope.launch { when (val r = op()) {
            is OperationResult.Success -> updateState { it.content().copy(game = r.value, loadState = UiLoadState.Loaded) }
            is OperationResult.Failure -> fail(r.error)
        }}
    }
    private fun mutateProposal(op: suspend () -> OperationResult<TimeProposal>) {
        updateState { it.content().copy(loadState = UiLoadState.Loading) }
        viewModelScope.launch { when (val r = op()) {
            is OperationResult.Success -> updateState { it.content().copy(timeProposal = r.value, loadState = UiLoadState.Loaded) }
            is OperationResult.Failure -> fail(r.error)
        }}
    }
    private fun mutateChange(op: suspend () -> OperationResult<GameChange>) {
        updateState { it.content().copy(loadState = UiLoadState.Loading) }
        viewModelScope.launch { when (val r = op()) {
            is OperationResult.Success -> updateState { it.content().copy(gameChange = r.value, loadState = UiLoadState.Loaded) }
            is OperationResult.Failure -> fail(r.error)
        }}
    }
    private fun fail(error: AppError) = updateState { c -> val s = c.content(); GameUiState.Error(error, s.game, s.timeProposal, s.gameChange, s.draft, s.proposalDraft) }
    private fun GameUiState.content() = when (this) {
        is GameUiState.Content -> this
        is GameUiState.Error -> GameUiState.Content(game, timeProposal, gameChange, draft, proposalDraft, loadState)
    }
    private fun GameUiState.draft() = when (this) { is GameUiState.Content -> draft; is GameUiState.Error -> draft }
    private fun GameUiState.proposalDraft() = when (this) { is GameUiState.Content -> proposalDraft; is GameUiState.Error -> proposalDraft }
}
