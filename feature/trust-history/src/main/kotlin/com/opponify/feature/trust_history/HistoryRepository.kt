package com.opponify.feature.trust_history

import com.opponify.common.architecture.OperationResult
import com.opponify.model.AttendanceDraft
import com.opponify.model.AttendanceEvent
import com.opponify.model.Dispute
import com.opponify.model.HistoricalRecord
import com.opponify.model.Result
import com.opponify.model.ResultDraft
import com.opponify.model.DisputeType
import java.util.UUID

interface HistoryRepository {
    suspend fun getHistory(gameId: UUID): OperationResult<HistoricalRecord>
    suspend fun submitAttendance(participantId: UUID, draft: AttendanceDraft, idempotencyKey: String): OperationResult<AttendanceEvent>
    suspend fun submitResult(gameId: UUID, draft: ResultDraft, idempotencyKey: String): OperationResult<Result>
    suspend fun openDispute(gameId: UUID, disputeType: DisputeType, reason: String, idempotencyKey: String): OperationResult<Dispute>
}
