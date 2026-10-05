package com.opponify.feature.facility

import com.opponify.common.architecture.OperationResult
import com.opponify.model.Facility
import com.opponify.model.FacilityQuery
import com.opponify.model.FacilitySuggestion

interface FacilityRepository {
    suspend fun discover(query: FacilityQuery): OperationResult<List<Facility>>
    suspend fun getFacility(id: java.util.UUID): OperationResult<Facility>
    suspend fun suggest(suggestion: FacilitySuggestion, idempotencyKey: String): OperationResult<FacilitySuggestion>
}
