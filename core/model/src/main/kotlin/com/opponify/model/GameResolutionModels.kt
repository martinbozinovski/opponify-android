package com.opponify.model

import java.time.Instant
import java.util.UUID

enum class AttendanceStatus {
    EXPECTED, CLAIMED_ATTENDED, CLAIMED_ABSENT, CONFIRMED_ATTENDED, CONFIRMED_ABSENT, DISPUTED, UNRESOLVED
}

enum class GameOutcomeStatus { PLAYED_NO_RESULT, PLAYED_RESULT_SUBMITTED, PLAYED_RESULT_CONFIRMED, PLAYED_RESULT_DISPUTED, NOT_PLAYED }

enum class DisputeType { ATTENDANCE, RESULT, GAME_OUTCOME }

enum class DisputeStatus { OPEN, RESOLVED, UNRESOLVED }

data class AttendanceEvent(
    val id: UUID,
    val scheduledGameId: UUID,
    val participantId: UUID,
    val status: AttendanceStatus,
    val submittedByUserId: UUID?,
    val submittedAt: Instant,
)

data class Result(
    val id: UUID,
    val scheduledGameId: UUID,
    val submittedByParticipantId: UUID,
    val sportCode: String,
    val payload: Map<String, String>,
    val status: GameOutcomeStatus,
    val submittedAt: Instant,
)

data class Dispute(
    val id: UUID,
    val scheduledGameId: UUID,
    val type: DisputeType,
    val status: DisputeStatus,
    val openedByParticipantId: UUID,
    val resolutionNote: String? = null,
)

data class HistoricalRecord(
    val id: UUID,
    val scheduledGameId: UUID,
    val outcomeStatus: GameOutcomeStatus,
    val participantIds: List<UUID>,
    val attendance: List<AttendanceEvent>,
    val result: Result?,
    val disputes: List<Dispute>,
    val authoritative: Boolean = true,
)

data class AttendanceDraft(
    val status: AttendanceStatus = AttendanceStatus.CLAIMED_ATTENDED,
)

data class ResultDraft(
    val sportCode: String = "",
    val payload: Map<String, String> = emptyMap(),
)
