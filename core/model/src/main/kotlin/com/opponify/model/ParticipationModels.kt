package com.opponify.model

import java.time.Instant
import java.util.UUID

enum class OpportunityRequestStatus { PENDING, ACCEPTED, REJECTED, WITHDRAWN, EXPIRED }
enum class ParticipationActorType { INDIVIDUAL, TEAM }
enum class AcceptedParticipationState { SCHEDULED, AWAITING_EXACT_TIME }

data class OpportunityRequest(
    val id: UUID,
    val opportunityId: UUID,
    val requesterUserId: UUID?,
    val requesterTeamId: UUID?,
    val actorType: ParticipationActorType,
    val message: String?,
    val status: OpportunityRequestStatus,
    val createdAt: Instant,
    val expiresAt: Instant?,
)

data class ParticipationDraft(
    val message: String = "",
)

data class AcceptedParticipation(
    val requestId: UUID,
    val opportunityId: UUID,
    val participantUserId: UUID?,
    val participantTeamId: UUID?,
    val actorType: ParticipationActorType,
    val state: AcceptedParticipationState,
    val scheduledGameId: UUID?,
)
