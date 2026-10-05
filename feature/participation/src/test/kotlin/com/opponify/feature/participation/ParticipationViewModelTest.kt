package com.opponify.feature.participation

import com.opponify.common.architecture.OperationResult
import com.opponify.model.AcceptedParticipation
import com.opponify.model.AcceptedParticipationState
import com.opponify.model.OpportunityRequest
import com.opponify.model.OpportunityRequestStatus
import com.opponify.model.ParticipationActorType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.time.Instant
import java.util.UUID
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class ParticipationViewModelTest {
    private val dispatcher = StandardTestDispatcher()
    private lateinit var repository: FakeParticipationRepository

    @Before fun setUp() {
        Dispatchers.setMain(dispatcher)
        repository = FakeParticipationRepository()
    }

    @After fun tearDown() { Dispatchers.resetMain() }

    @Test fun load_keeps_requests_as_requests() = runTest {
        val opportunityId = UUID.randomUUID()
        repository.requests = listOf(repository.request.copy(opportunityId = opportunityId))
        val vm = ParticipationViewModel(repository)
        vm.loadRequests(opportunityId)
        advanceUntilIdle()
        val state = vm.uiState.value as ParticipationUiState.Content
        assertEquals(1, state.requests.size)
        assertEquals(OpportunityRequestStatus.PENDING, state.requests.single().status)
        assertEquals(null, state.accepted)
    }

    @Test fun accept_uses_server_result_and_does_not_infer_scheduling() = runTest {
        val vm = ParticipationViewModel(repository)
        vm.loadRequests(repository.request.opportunityId)
        advanceUntilIdle()
        vm.acceptRequest(repository.request.id, "key-1")
        advanceUntilIdle()
        val state = vm.uiState.value as ParticipationUiState.Content
        assertEquals(AcceptedParticipationState.AWAITING_EXACT_TIME, state.accepted?.state)
        assertTrue(repository.acceptCalls == 1)
    }

    private class FakeParticipationRepository : ParticipationRepository {
        val opportunityId = UUID.randomUUID()
        val request = OpportunityRequest(UUID.randomUUID(), opportunityId, UUID.randomUUID(), null, ParticipationActorType.INDIVIDUAL, "hello", OpportunityRequestStatus.PENDING, Instant.now(), null)
        var requests = listOf(request)
        var acceptCalls = 0
        override suspend fun listRequests(opportunityId: UUID) = OperationResult.Success(requests.filter { it.opportunityId == opportunityId })
        override suspend fun listMyRequests() = OperationResult.Success(requests)
        override suspend fun createRequest(opportunityId: UUID, draft: com.opponify.model.ParticipationDraft, idempotencyKey: String) = OperationResult.Success(request.copy(opportunityId = opportunityId, message = draft.message))
        override suspend fun withdrawRequest(requestId: UUID, idempotencyKey: String) = OperationResult.Success(request.copy(status = OpportunityRequestStatus.WITHDRAWN))
        override suspend fun rejectRequest(requestId: UUID, idempotencyKey: String) = OperationResult.Success(request.copy(status = OpportunityRequestStatus.REJECTED))
        override suspend fun acceptRequest(requestId: UUID, idempotencyKey: String): OperationResult.Success<AcceptedParticipation> {
            acceptCalls++
            return OperationResult.Success(AcceptedParticipation(requestId, request.opportunityId, request.requesterUserId, request.requesterTeamId, request.actorType, com.opponify.model.AcceptedParticipationState.AWAITING_EXACT_TIME, null))
        }
    }
}
