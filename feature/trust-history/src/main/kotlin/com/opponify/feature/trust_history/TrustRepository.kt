package com.opponify.feature.trust_history

import com.opponify.common.architecture.OperationResult
import com.opponify.model.Dispute
import com.opponify.model.DisputeResolutionDraft
import com.opponify.model.TrustAssessment
import java.util.UUID

interface TrustRepository {
    suspend fun getTrustAssessment(subjectId: UUID): OperationResult<TrustAssessment>
    suspend fun getDisputes(subjectId: UUID): OperationResult<List<Dispute>>
    suspend fun resolveDispute(
        disputeId: UUID,
        draft: DisputeResolutionDraft,
        idempotencyKey: String,
    ): OperationResult<Dispute>
}
