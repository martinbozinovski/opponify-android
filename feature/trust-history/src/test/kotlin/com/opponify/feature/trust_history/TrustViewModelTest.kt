package com.opponify.feature.trust_history

import com.opponify.common.architecture.OperationResult
import com.opponify.model.Dispute
import com.opponify.model.DisputeResolutionDraft
import com.opponify.model.DisputeStatus
import com.opponify.model.DisputeType
import com.opponify.model.TrustAssessment
import com.opponify.model.TrustCategory
import com.opponify.model.TrustEvidenceSummary
import com.opponify.model.TrustSubjectType
import java.time.Instant
import java.util.UUID
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

@OptIn(ExperimentalCoroutinesApi::class)
class TrustViewModelTest {
    private val dispatcher = StandardTestDispatcher()
    private lateinit var repository: FakeTrustRepository

    @Before
    fun setUp() { Dispatchers.setMain(dispatcher); repository = FakeTrustRepository() }

    @After
    fun tearDown() { Dispatchers.resetMain() }

    @Test
    fun loadExposesAssessmentAndDisputes() = runTest {
        val vm = TrustViewModel(repository)
        vm.load(repository.subjectId)
        advanceUntilIdle()
        val state = vm.uiState.value as TrustUiState.Content
        assertEquals(TrustCategory.RELIABLE, state.assessment?.category)
        assertEquals(1, state.disputes.size)
    }

    @Test
    fun staleAssessmentIsExposedAsStaleLoadState() = runTest {
        repository.assessment = repository.assessment.copy(isStale = true)
        val vm = TrustViewModel(repository)
        vm.load(repository.subjectId)
        advanceUntilIdle()
        assertTrue((vm.uiState.value as TrustUiState.Content).loadState is com.opponify.common.architecture.UiLoadState.Stale)
    }

    @Test
    fun resolvingDisputeUsesIdempotencyAndDoesNotCalculateTrust() = runTest {
        val vm = TrustViewModel(repository)
        vm.load(repository.subjectId)
        advanceUntilIdle()
        vm.updateDisputeNote("Participants confirmed the game was played.")
        vm.resolveDispute(repository.dispute.id, "idem-10i")
        advanceUntilIdle()
        assertEquals("idem-10i", repository.lastIdempotencyKey)
        assertEquals(DisputeStatus.RESOLVED, (vm.uiState.value as TrustUiState.Content).disputes.single().status)
        assertEquals(0, repository.trustCalculationCalls)
    }

    private class FakeTrustRepository : TrustRepository {
        val subjectId = UUID.randomUUID()
        val disputeId = UUID.randomUUID()
        var trustCalculationCalls = 0
        var lastIdempotencyKey: String? = null
        var assessment = TrustAssessment(
            subjectId = subjectId,
            subjectType = TrustSubjectType.PLAYER,
            score = 84,
            category = TrustCategory.RELIABLE,
            evidenceSummary = TrustEvidenceSummary(confirmedGames = 12, confirmedNoShows = 1),
            methodologyVersion = "v1",
            calculatedAt = Instant.now(),
        )
        var dispute = Dispute(disputeId, UUID.randomUUID(), DisputeType.ATTENDANCE, DisputeStatus.OPEN, UUID.randomUUID())

        override suspend fun getTrustAssessment(subjectId: UUID): OperationResult<TrustAssessment> = OperationResult.Success(assessment)
        override suspend fun getDisputes(subjectId: UUID): OperationResult<List<Dispute>> = OperationResult.Success(listOf(dispute))
        override suspend fun resolveDispute(disputeId: UUID, draft: DisputeResolutionDraft, idempotencyKey: String): OperationResult<Dispute> {
            lastIdempotencyKey = idempotencyKey
            dispute = dispute.copy(status = DisputeStatus.RESOLVED, resolutionNote = draft.resolutionNote)
            return OperationResult.Success(dispute)
        }
    }
}
