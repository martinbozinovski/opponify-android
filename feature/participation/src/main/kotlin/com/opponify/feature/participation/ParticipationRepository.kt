package com.opponify.feature.participation

import com.opponify.common.architecture.OperationResult
import com.opponify.model.AcceptedParticipation
import com.opponify.model.OpportunityRequest
import com.opponify.model.ParticipationDraft
import java.util.UUID

interface ParticipationRepository {
    suspend fun listRequests(opportunityId: UUID): OperationResult<List<OpportunityRequest>>
    suspend fun listMyRequests(): OperationResult<List<OpportunityRequest>>
    suspend fun createRequest(opportunityId: UUID, draft: ParticipationDraft, idempotencyKey: String): OperationResult<OpportunityRequest>
    suspend fun withdrawRequest(requestId: UUID, idempotencyKey: String): OperationResult<OpportunityRequest>
    suspend fun acceptRequest(requestId: UUID, idempotencyKey: String): OperationResult<AcceptedParticipation>
    suspend fun rejectRequest(requestId: UUID, idempotencyKey: String): OperationResult<OpportunityRequest>
}
