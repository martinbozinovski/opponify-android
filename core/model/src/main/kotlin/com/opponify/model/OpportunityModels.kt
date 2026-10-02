package com.opponify.model

import java.time.Instant
import java.util.UUID

enum class OpportunityNeed { OPPONENT, PLAYERS, GAME }
enum class OpportunityTimeType { EXACT, RANGE, FLEXIBLE }
enum class OpportunityStatus { DRAFT, OPEN, CLOSED, EXPIRED, CANCELLED }
enum class DiscoverySort { RELEVANCE, DATE, DISTANCE, CREATED }

data class Opportunity(
    val id: UUID,
    val creatorUserId: UUID?,
    val creatorTeamId: UUID?,
    val sportId: UUID,
    val need: OpportunityNeed,
    val town: String,
    val timeType: OpportunityTimeType,
    val startAt: Instant?,
    val endAt: Instant?,
    val level: SkillLevel?,
    val desiredOpponentLevel: SkillLevel?,
    val facilityId: UUID?,
    val freeFormLocation: String?,
    val additionalInfo: String?,
    val targetCapacity: Int,
    val minimumRequired: Int,
    val status: OpportunityStatus,
)

data class OpportunityDraft(
    val creatorUserId: UUID? = null,
    val creatorTeamId: UUID? = null,
    val sportId: UUID? = null,
    val need: OpportunityNeed = OpportunityNeed.OPPONENT,
    val town: String = "",
    val timeType: OpportunityTimeType = OpportunityTimeType.EXACT,
    val startAt: Instant? = null,
    val endAt: Instant? = null,
    val level: SkillLevel? = null,
    val desiredOpponentLevel: SkillLevel? = null,
    val facilityId: UUID? = null,
    val freeFormLocation: String = "",
    val additionalInfo: String = "",
    val targetCapacity: Int = 2,
    val minimumRequired: Int = 2,
)

data class DiscoveryQuery(
    val sportId: UUID? = null,
    val need: OpportunityNeed? = null,
    val town: String? = null,
    val level: SkillLevel? = null,
    val desiredOpponentLevel: SkillLevel? = null,
    val radiusKm: Int? = null,
    val sort: DiscoverySort = DiscoverySort.RELEVANCE,
    val cursor: String? = null,
)
