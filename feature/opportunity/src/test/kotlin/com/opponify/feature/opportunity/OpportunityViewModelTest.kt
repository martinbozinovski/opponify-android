package com.opponify.feature.opportunity

import com.opponify.common.architecture.OperationResult
import com.opponify.model.Opportunity
import com.opponify.model.OpportunityDraft
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
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.util.UUID

@OptIn(ExperimentalCoroutinesApi::class)
class OpportunityViewModelTest {
    private val dispatcher = StandardTestDispatcher()
    private lateinit var repository: FakeRepository

    @Before fun setUp() { Dispatchers.setMain(dispatcher); repository = FakeRepository() }
    @After fun tearDown() { Dispatchers.resetMain() }

    @Test fun create_keeps_opportunity_as_non_commitment() = runTest(dispatcher) {
        val viewModel = OpportunityViewModel(repository)
        viewModel.updateDraft { it.copy(sportId = UUID.randomUUID(), town = "Skopje", need = OpportunityNeed.OPPONENT, timeType = OpportunityTimeType.FLEXIBLE) }
        viewModel.create("key")
        advanceUntilIdle()
        val state = viewModel.uiState.value as OpportunityUiState.Content
        assertEquals(OpportunityStatus.OPEN, state.opportunity.status)
        assertEquals(OpportunityTimeType.FLEXIBLE, state.opportunity.timeType)
    }

    private class FakeRepository : OpportunityRepository {
        override suspend fun createOpportunity(draft: OpportunityDraft, idempotencyKey: String) = OperationResult.Success(
            Opportunity(UUID.randomUUID(), draft.creatorUserId, draft.creatorTeamId, draft.sportId!!, draft.need, draft.town, draft.timeType, draft.startAt, draft.endAt, draft.level, draft.desiredOpponentLevel, draft.facilityId, draft.freeFormLocation, draft.additionalInfo, draft.targetCapacity, draft.minimumRequired, OpportunityStatus.OPEN)
        )
        override suspend fun getOpportunity(id: UUID) = error("unused")
        override suspend fun updateOpportunity(id: UUID, draft: OpportunityDraft, idempotencyKey: String) = error("unused")
        override suspend fun reopenOpportunity(id: UUID, idempotencyKey: String) = error("unused")
        override suspend fun cancelOpportunity(id: UUID, idempotencyKey: String) = error("unused")
    }
}
