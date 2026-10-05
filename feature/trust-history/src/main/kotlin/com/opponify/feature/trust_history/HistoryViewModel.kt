package com.opponify.feature.trust_history

import androidx.lifecycle.viewModelScope
import com.opponify.common.architecture.AppError
import com.opponify.common.architecture.BaseViewModel
import com.opponify.common.architecture.OperationResult
import com.opponify.common.architecture.UiLoadState
import com.opponify.model.AttendanceDraft
import com.opponify.model.AttendanceEvent
import com.opponify.model.Dispute
import com.opponify.model.DisputeType
import com.opponify.model.HistoricalRecord
import com.opponify.model.Result
import com.opponify.model.ResultDraft
import kotlinx.coroutines.launch
import java.util.UUID

sealed interface HistoryUiState {
    val history: HistoricalRecord?
    val attendance: AttendanceEvent?
    val result: Result?
    val dispute: Dispute?
    val loadState: UiLoadState
    val attendanceDraft: AttendanceDraft
    val resultDraft: ResultDraft
    data class Content(override val history: HistoricalRecord?=null, override val attendance: AttendanceEvent?=null, override val result: Result?=null, override val dispute: Dispute?=null, override val loadState: UiLoadState=UiLoadState.Initial, override val attendanceDraft: AttendanceDraft=AttendanceDraft(), override val resultDraft: ResultDraft=ResultDraft()) : HistoryUiState
    data class Error(val error: AppError, override val history: HistoricalRecord?=null, override val attendance: AttendanceEvent?=null, override val result: Result?=null, override val dispute: Dispute?=null, override val loadState: UiLoadState=UiLoadState.Error(error), override val attendanceDraft: AttendanceDraft=AttendanceDraft(), override val resultDraft: ResultDraft=ResultDraft()) : HistoryUiState
}

class HistoryViewModel(private val repository: HistoryRepository) : BaseViewModel<HistoryUiState>(HistoryUiState.Content()) {
    fun load(gameId: UUID) { updateState { it.content().copy(loadState=UiLoadState.Loading) }; viewModelScope.launch { when(val r=repository.getHistory(gameId)){ is OperationResult.Success -> updateState{it.content().copy(history=r.value,loadState=UiLoadState.Loaded)}; is OperationResult.Failure -> fail(r.error)}} }
    fun updateAttendance(transform:(AttendanceDraft)->AttendanceDraft)=updateState{it.content().copy(attendanceDraft=transform(it.attendanceDraft))}
    fun updateResult(transform:(ResultDraft)->ResultDraft)=updateState{it.content().copy(resultDraft=transform(it.resultDraft))}
    fun submitAttendance(participantId:UUID,idempotencyKey:String)=mutateAttendance{repository.submitAttendance(participantId,uiState.value.attendanceDraft(),idempotencyKey)}
    fun submitResult(gameId:UUID,idempotencyKey:String)=mutateResult{repository.submitResult(gameId,uiState.value.resultDraft(),idempotencyKey)}
    fun openDispute(gameId:UUID,type:DisputeType,reason:String,idempotencyKey:String)=mutateDispute{repository.openDispute(gameId,type,reason,idempotencyKey)}
    private fun mutateAttendance(op:suspend()->OperationResult<AttendanceEvent>){updateState{it.content().copy(loadState=UiLoadState.Loading)};viewModelScope.launch{when(val r=op()){is OperationResult.Success->updateState{it.content().copy(attendance=r.value,loadState=UiLoadState.Loaded)};is OperationResult.Failure->fail(r.error)}}}
    private fun mutateResult(op:suspend()->OperationResult<Result>){updateState{it.content().copy(loadState=UiLoadState.Loading)};viewModelScope.launch{when(val r=op()){is OperationResult.Success->updateState{it.content().copy(result=r.value,loadState=UiLoadState.Loaded)};is OperationResult.Failure->fail(r.error)}}}
    private fun mutateDispute(op:suspend()->OperationResult<Dispute>){updateState{it.content().copy(loadState=UiLoadState.Loading)};viewModelScope.launch{when(val r=op()){is OperationResult.Success->updateState{it.content().copy(dispute=r.value,loadState=UiLoadState.Loaded)};is OperationResult.Failure->fail(r.error)}}}
    private fun fail(error:AppError)=updateState { c -> val s=c.content(); HistoryUiState.Error(error,s.history,s.attendance,s.result,s.dispute,UiLoadState.Error(error),s.attendanceDraft,s.resultDraft) }
    private fun HistoryUiState.content()=when(this){is HistoryUiState.Content->this;is HistoryUiState.Error->HistoryUiState.Content(history,attendance,result,dispute,loadState,attendanceDraft,resultDraft)}
    private fun HistoryUiState.attendanceDraft()=when(this){is HistoryUiState.Content->attendanceDraft;is HistoryUiState.Error->attendanceDraft}
    private fun HistoryUiState.resultDraft()=when(this){is HistoryUiState.Content->resultDraft;is HistoryUiState.Error->resultDraft}
}
