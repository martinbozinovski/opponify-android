package com.opponify.model

import java.time.Duration
import java.time.Instant
import java.time.ZoneId
import java.util.UUID

enum class ScheduledGameStatus { SCHEDULED, IN_PROGRESS, AWAITING_RESOLUTION, CANCELLED }
enum class GameParticipantStatus { EXPECTED, CANCELLED, REPLACED }
enum class TimeProposalStatus { PROPOSED, CONFIRMED, REJECTED, EXPIRED, SUPERSEDED }
enum class GameChangeStatus { PROPOSED, CONFIRMED, REJECTED, EXPIRED, SUPERSEDED }

data class ScheduledGame(
    val id: UUID,
    val opportunityId: UUID,
    val startAt: Instant,
    val duration: Duration,
    val endAt: Instant,
    val timeZone: ZoneId,
    val status: ScheduledGameStatus = ScheduledGameStatus.SCHEDULED,
    val participants: List<GameParticipant> = emptyList(),
)

data class GameParticipant(
    val id: UUID,
    val scheduledGameId: UUID,
    val userId: UUID?,
    val teamId: UUID?,
    val actorType: ParticipationActorType,
    val status: GameParticipantStatus = GameParticipantStatus.EXPECTED,
)

data class TimeProposal(
    val id: UUID,
    val scheduledGameId: UUID,
    val proposedStartAt: Instant,
    val proposedDuration: Duration,
    val proposedEndAt: Instant,
    val proposedTimeZone: ZoneId,
    val proposerId: UUID?,
    val status: TimeProposalStatus,
    val affectedParticipantIds: List<UUID>,
)

data class GameChange(
    val id: UUID,
    val scheduledGameId: UUID,
    val previousStartAt: Instant,
    val proposedStartAt: Instant,
    val previousDuration: Duration,
    val proposedDuration: Duration,
    val status: GameChangeStatus,
    val affectedParticipantIds: List<UUID>,
)

data class GameDraft(
    val startAt: Instant? = null,
    val duration: Duration? = null,
    val timeZone: ZoneId = ZoneId.of("UTC"),
)

data class TimeProposalDraft(
    val startAt: Instant? = null,
    val duration: Duration? = null,
    val timeZone: ZoneId = ZoneId.of("UTC"),
)
