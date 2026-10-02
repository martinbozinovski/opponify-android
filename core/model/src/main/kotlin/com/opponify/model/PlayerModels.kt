package com.opponify.model

import java.util.UUID

enum class SkillLevel { EASY, MEDIUM, HARD }

data class PlayerProfile(
    val userId: UUID,
    val displayName: String,
    val skillLevel: SkillLevel?,
    val desiredOpponentLevel: SkillLevel?,
    val town: String?,
    val bio: String?,
)

data class PlayerProfileDraft(
    val displayName: String = "",
    val skillLevel: SkillLevel? = null,
    val desiredOpponentLevel: SkillLevel? = null,
    val town: String = "",
    val bio: String = "",
)
