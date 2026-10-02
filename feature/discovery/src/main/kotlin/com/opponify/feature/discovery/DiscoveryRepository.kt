package com.opponify.feature.discovery

import com.opponify.common.architecture.OperationResult
import com.opponify.model.DiscoveryQuery
import com.opponify.model.Opportunity

interface DiscoveryRepository {
    suspend fun discover(query: DiscoveryQuery): OperationResult<List<Opportunity>>
}
