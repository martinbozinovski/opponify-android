package com.opponify.feature.trust_history

import com.opponify.common.architecture.OperationResult
import com.opponify.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.time.Instant
import java.util.UUID

@OptIn(ExperimentalCoroutinesApi::class)
class HistoryViewModelTest { private val dispatcher=StandardTestDispatcher(); private lateinit var repository:FakeHistoryRepository; @Before fun setUp(){Dispatchers.setMain(dispatcher);repository=FakeHistoryRepository()};@After fun tearDown(){Dispatchers.resetMain()}
@Test fun load_keeps_authoritative_history()=runTest(dispatcher){val vm=HistoryViewModel(repository);vm.load(repository.history.scheduledGameId);advanceUntilIdle();assertEquals(repository.history,(vm.uiState.value as HistoryUiState.Content).history)}
@Test fun attendance_waits_for_server_result()=runTest(dispatcher){val vm=HistoryViewModel(repository);vm.submitAttendance(repository.participantId,"idem-attendance");advanceUntilIdle();assertEquals(repository.attendance,(vm.uiState.value as HistoryUiState.Content).attendance)}
private class FakeHistoryRepository:HistoryRepository{val participantId=UUID.randomUUID();val gameId=UUID.randomUUID();val attendance=AttendanceEvent(UUID.randomUUID(),gameId,participantId,AttendanceStatus.CLAIMED_ATTENDED,UUID.randomUUID(),Instant.parse("2026-10-10T20:00:00Z"));val history=HistoricalRecord(UUID.randomUUID(),gameId,GameOutcomeStatus.PLAYED_NO_RESULT,listOf(participantId),listOf(attendance),null,emptyList());override suspend fun getHistory(gameId:UUID)=OperationResult.Success(history);override suspend fun submitAttendance(participantId:UUID,draft:AttendanceDraft,idempotencyKey:String)=OperationResult.Success(attendance);override suspend fun submitResult(gameId:UUID,draft:ResultDraft,idempotencyKey:String):OperationResult<Result>=OperationResult.Success(Result(UUID.randomUUID(),gameId,participantId,draft.sportCode,draft.payload,GameOutcomeStatus.PLAYED_RESULT_SUBMITTED,Instant.parse("2026-10-10T20:01:00Z")));override suspend fun openDispute(gameId:UUID,disputeType:DisputeType,reason:String,idempotencyKey:String):OperationResult<Dispute>=OperationResult.Success(Dispute(UUID.randomUUID(),gameId,disputeType,DisputeStatus.OPEN,participantId,reason))}}
