package com.opponify.feature.discovery

import com.opponify.common.architecture.OperationResult
import com.opponify.model.DiscoveryQuery
import com.opponify.model.Opportunity
import com.opponify.model.OpportunityNeed
import com.opponify.model.OpportunityStatus
import com.opponify.model.OpportunityTimeType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.util.UUID

@OptIn(ExperimentalCoroutinesApi::class)
class DiscoveryViewModelTest {
    private val dispatcher = StandardTestDispatcher()
    private val opportunity = Opportunity(UUID.randomUUID(), UUID.randomUUID(), null, UUID.randomUUID(), OpportunityNeed.GAME, "Skopje", OpportunityTimeType.EXACT, null, null, null, null, null, null, null, 2, 2, OpportunityStatus.OPEN)

    @Before fun setUp() { Dispatchers.setMain(dispatcher) }
    @After fun tearDown() { Dispatchers.resetMain() }

    @Test fun load_preserves_explicit_query_and_results() = runTest(dispatcher) {
        val repository = object : DiscoveryRepository { override suspend fun discover(query: DiscoveryQuery) = OperationResult.Success(listOf(opportunity)) }
        val query = DiscoveryQuery(town = "Skopje")
        val viewModel = DiscoveryViewModel(repository)
        viewModel.load(query)
        advanceUntilIdle()
        val state = viewModel.uiState.value as DiscoveryUiState.Content
        assertEquals(query, state.query)
        assertEquals(1, state.opportunities.size)
    }
}
