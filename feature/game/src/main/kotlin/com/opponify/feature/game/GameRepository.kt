package com.opponify.feature.game

import com.opponify.common.architecture.OperationResult
import com.opponify.model.GameChange
import com.opponify.model.GameDraft
import com.opponify.model.ScheduledGame
import com.opponify.model.TimeProposal
import com.opponify.model.TimeProposalDraft
import java.util.UUID

interface GameRepository {
    suspend fun getGame(gameId: UUID): OperationResult<ScheduledGame>
    suspend fun scheduleGame(opportunityId: UUID, draft: GameDraft, idempotencyKey: String): OperationResult<ScheduledGame>
    suspend fun proposeTime(gameId: UUID, draft: TimeProposalDraft, idempotencyKey: String): OperationResult<TimeProposal>
    suspend fun confirmTimeProposal(proposalId: UUID, idempotencyKey: String): OperationResult<ScheduledGame>
    suspend fun rejectTimeProposal(proposalId: UUID, idempotencyKey: String): OperationResult<TimeProposal>
    suspend fun proposeMaterialChange(gameId: UUID, draft: TimeProposalDraft, idempotencyKey: String): OperationResult<GameChange>
    suspend fun confirmMaterialChange(changeId: UUID, idempotencyKey: String): OperationResult<ScheduledGame>
    suspend fun rejectMaterialChange(changeId: UUID, idempotencyKey: String): OperationResult<GameChange>
    suspend fun cancelGame(gameId: UUID, reason: String?, idempotencyKey: String): OperationResult<ScheduledGame>
}
