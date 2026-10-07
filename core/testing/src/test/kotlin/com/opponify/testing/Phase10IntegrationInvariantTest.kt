package com.opponify.testing

import com.opponify.model.*
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.Duration
import java.time.Instant
import java.time.ZoneId
import java.util.UUID

class Phase10IntegrationInvariantTest {
    @Test fun opportunityAcceptanceDoesNotCreateCommitmentForFlexibleTime() {
        val accepted = AcceptedParticipation(
            requestId = UUID.randomUUID(),
            opportunityId = UUID.randomUUID(),
            participantUserId = UUID.randomUUID(),
            participantTeamId = null,
            actorType = ParticipationActorType.INDIVIDUAL,
            state = AcceptedParticipationState.AWAITING_EXACT_TIME,
            scheduledGameId = null,
        )
        assertEquals(AcceptedParticipationState.AWAITING_EXACT_TIME, accepted.state)
        assertNull(accepted.scheduledGameId)
    }

    @Test fun scheduledGameHasAuthoritativeExactInterval() {
        val start = Instant.parse("2026-10-06T18:00:00Z")
        val duration = Duration.ofMinutes(90)
        val game = ScheduledGame(
            id = UUID.randomUUID(), opportunityId = UUID.randomUUID(),
            startAt = start, duration = duration, endAt = start.plus(duration),
            timeZone = ZoneId.of("Europe/Skopje"),
        )
        assertEquals(start.plus(duration), game.endAt)
        assertEquals(Duration.ofMinutes(90), game.duration)
    }

    @Test fun attendanceClaimsRemainDistinctFromConfirmedFacts() {
        val event = AttendanceEvent(
            id = UUID.randomUUID(), scheduledGameId = UUID.randomUUID(), participantId = UUID.randomUUID(),
            status = AttendanceStatus.CLAIMED_ABSENT, submittedByUserId = UUID.randomUUID(),
            submittedAt = Instant.now(),
        )
        assertTrue(event.status != AttendanceStatus.CONFIRMED_ABSENT)
        assertTrue(event.status == AttendanceStatus.CLAIMED_ABSENT)
    }

    @Test fun disputedResultIsNotConfirmed() {
        val result = Result(
            id = UUID.randomUUID(), scheduledGameId = UUID.randomUUID(), submittedByParticipantId = UUID.randomUUID(),
            sportCode = "tennis", payload = mapOf("sets" to "6-4,3-6,7-5"),
            status = GameOutcomeStatus.PLAYED_RESULT_DISPUTED, submittedAt = Instant.now(),
        )
        assertEquals(GameOutcomeStatus.PLAYED_RESULT_DISPUTED, result.status)
        assertTrue(result.status != GameOutcomeStatus.PLAYED_RESULT_CONFIRMED)
    }

    @Test fun playerAndTeamTrustRemainSeparateSubjects() {
        val playerId = UUID.randomUUID()
        val teamId = UUID.randomUUID()
        val player = TrustAssessment(playerId, TrustSubjectType.PLAYER, 92, TrustCategory.EXCELLENT, TrustEvidenceSummary(38), "v1", Instant.now())
        val team = TrustAssessment(teamId, TrustSubjectType.TEAM, 76, TrustCategory.VERY_RELIABLE, TrustEvidenceSummary(20), "v1", Instant.now())
        assertEquals(TrustSubjectType.PLAYER, player.subjectType)
        assertEquals(TrustSubjectType.TEAM, team.subjectType)
        assertTrue(player.subjectId != team.subjectId)
    }

    @Test fun facilityDoesNotEncodeReservationOrAvailability() {
        val facility = Facility(
            id = UUID.randomUUID(), name = "Test Court", town = "Skopje", address = null,
            location = GeoPoint(42.0, 21.4), status = FacilityStatus.APPROVED,
        )
        assertEquals(FacilityStatus.APPROVED, facility.status)
        assertTrue(facility.supportedSportIds.isEmpty())
    }

    @Test fun notificationIsSignalAndDoesNotContainDomainStateMutation() {
        val notification = Notification(
            id = UUID.randomUUID(), type = NotificationType.GAME_CANCELLED,
            title = "Game cancelled", body = "A game was cancelled", createdAt = Instant.now(),
            entityId = UUID.randomUUID(),
        )
        assertEquals(NotificationType.GAME_CANCELLED, notification.type)
        assertTrue(notification.entityId != null)
    }

    @Test fun communicationEligibilityIsExplicit() {
        val conversation = Conversation(UUID.randomUUID(), listOf(UUID.randomUUID(), UUID.randomUUID()), eligible = false)
        assertFalse(conversation.eligible)
    }

    @Test fun historicalRecordPreservesDisputeAndAttendance() {
        val gameId = UUID.randomUUID()
        val participantId = UUID.randomUUID()
        val attendance = AttendanceEvent(UUID.randomUUID(), gameId, participantId, AttendanceStatus.DISPUTED, null, Instant.now())
        val dispute = Dispute(UUID.randomUUID(), gameId, DisputeType.ATTENDANCE, DisputeStatus.OPEN, participantId)
        val history = HistoricalRecord(
            UUID.randomUUID(), gameId, GameOutcomeStatus.PLAYED_NO_RESULT, listOf(participantId), listOf(attendance), null, listOf(dispute)
        )
        assertEquals(1, history.attendance.size)
        assertEquals(1, history.disputes.size)
        assertTrue(history.authoritative)
    }
}
