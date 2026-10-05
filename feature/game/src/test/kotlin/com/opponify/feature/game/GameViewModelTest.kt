package com.opponify.feature.game

import com.opponify.common.architecture.OperationResult
import com.opponify.model.GameDraft
import com.opponify.model.ScheduledGame
import com.opponify.model.ScheduledGameStatus
import com.opponify.model.TimeProposal
import com.opponify.model.TimeProposalDraft
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
import java.time.Duration
import java.time.Instant
import java.time.ZoneId
import java.util.UUID

@OptIn(ExperimentalCoroutinesApi::class)
class GameViewModelTest {
    private val dispatcher = StandardTestDispatcher()
    private lateinit var repository: FakeGameRepository

    @Before fun setUp() { Dispatchers.setMain(dispatcher); repository = FakeGameRepository() }
    @After fun tearDown() { Dispatchers.resetMain() }

    @Test fun load_keeps_server_game_as_state() = runTest(dispatcher) {
        val game = repository.game
        val vm = GameViewModel(repository)
        vm.load(game.id); advanceUntilIdle()
        assertEquals(game, (vm.uiState.value as GameUiState.Content).game)
    }

    @Test fun schedule_requires_server_result() = runTest(dispatcher) {
        val vm = GameViewModel(repository)
        vm.updateDraft { GameDraft(Instant.parse("2026-10-10T18:00:00Z"), Duration.ofMinutes(60), ZoneId.of("Europe/Skopje")) }
        vm.schedule(UUID.randomUUID(), "idem-1"); advanceUntilIdle()
        assertEquals(repository.game, (vm.uiState.value as GameUiState.Content).game)
    }

    private class FakeGameRepository : GameRepository {
        val game = ScheduledGame(UUID.randomUUID(), UUID.randomUUID(), Instant.parse("2026-10-10T18:00:00Z"), Duration.ofMinutes(60), Instant.parse("2026-10-10T19:00:00Z"), ZoneId.of("Europe/Skopje"), ScheduledGameStatus.SCHEDULED)
        override suspend fun getGame(gameId: UUID) = OperationResult.Success(game)
        override suspend fun scheduleGame(opportunityId: UUID, draft: GameDraft, idempotencyKey: String) = OperationResult.Success(game)
        override suspend fun proposeTime(gameId: UUID, draft: TimeProposalDraft, idempotencyKey: String): OperationResult<TimeProposal> = throw UnsupportedOperationException()
        override suspend fun confirmTimeProposal(proposalId: UUID, idempotencyKey: String) = OperationResult.Success(game)
        override suspend fun rejectTimeProposal(proposalId: UUID, idempotencyKey: String): OperationResult<TimeProposal> = throw UnsupportedOperationException()
        override suspend fun proposeMaterialChange(gameId: UUID, draft: TimeProposalDraft, idempotencyKey: String) = OperationResult.Success(com.opponify.model.GameChange(UUID.randomUUID(), gameId, game.startAt, game.startAt, game.duration, game.duration, com.opponify.model.GameChangeStatus.PROPOSED, emptyList()))
        override suspend fun confirmMaterialChange(changeId: UUID, idempotencyKey: String) = OperationResult.Success(game)
        override suspend fun rejectMaterialChange(changeId: UUID, idempotencyKey: String) = OperationResult.Success(com.opponify.model.GameChange(UUID.randomUUID(), game.id, game.startAt, game.startAt, game.duration, game.duration, com.opponify.model.GameChangeStatus.REJECTED, emptyList()))
        override suspend fun cancelGame(gameId: UUID, reason: String?, idempotencyKey: String) = OperationResult.Success(game.copy(status = ScheduledGameStatus.CANCELLED))
    }
}
