package com.opponify.model

import java.time.Instant
import java.util.UUID

enum class TrustSubjectType { PLAYER, TEAM }

enum class TrustCategory {
    NEW_PROVISIONAL,
    NEEDS_IMPROVEMENT,
    RELIABLE,
    VERY_RELIABLE,
    EXCELLENT,
}

enum class TrustEvidenceType {
    CONFIRMED_GAME,
    COMPLETED_WITHOUT_RESULT,
    CONFIRMED_NO_SHOW,
    LATE_CANCELLATION,
}

data class TrustEvidenceSummary(
    val confirmedGames: Int = 0,
    val completedWithoutResult: Int = 0,
    val confirmedNoShows: Int = 0,
    val lateCancellations: Int = 0,
)

data class TrustAssessment(
    val subjectId: UUID,
    val subjectType: TrustSubjectType,
    val score: Int?,
    val category: TrustCategory,
    val evidenceSummary: TrustEvidenceSummary,
    val methodologyVersion: String,
    val calculatedAt: Instant?,
    val isUpdating: Boolean = false,
    val isStale: Boolean = false,
)

data class TrustEvidence(
    val id: UUID,
    val subjectId: UUID,
    val subjectType: TrustSubjectType,
    val type: TrustEvidenceType,
    val sourceEventId: UUID,
    val eligible: Boolean,
    val occurredAt: Instant,
    val weight: Double,
)

data class DisputeResolutionDraft(
    val resolutionNote: String = "",
)
