package com.opponify.feature.opportunity

import com.opponify.common.architecture.OperationResult
import com.opponify.model.Opportunity
import com.opponify.model.OpportunityDraft
import java.util.UUID

interface OpportunityRepository {
    suspend fun createOpportunity(draft: OpportunityDraft, idempotencyKey: String): OperationResult<Opportunity>
    suspend fun getOpportunity(id: UUID): OperationResult<Opportunity>
    suspend fun updateOpportunity(id: UUID, draft: OpportunityDraft, idempotencyKey: String): OperationResult<Opportunity>
    suspend fun reopenOpportunity(id: UUID, idempotencyKey: String): OperationResult<Opportunity>
    suspend fun cancelOpportunity(id: UUID, idempotencyKey: String): OperationResult<Opportunity>
}
