package com.opponify.model

import java.util.UUID

enum class TeamRole { CAPTAIN, MANAGER, MEMBER }
enum class MembershipStatus { PENDING, ACTIVE, LEFT, REMOVED }

data class Team(
    val id: UUID,
    val name: String,
    val sportId: UUID,
    val skillLevel: SkillLevel?,
    val town: String?,
    val active: Boolean,
)

data class TeamMembership(
    val teamId: UUID,
    val userId: UUID,
    val role: TeamRole,
    val status: MembershipStatus,
)

data class TeamMember(
    val userId: UUID,
    val displayName: String,
    val role: TeamRole,
    val status: MembershipStatus,
)
