package com.opponify.model

import java.util.UUID

enum class FacilityStatus { SUGGESTED, UNDER_REVIEW, APPROVED, REJECTED, ARCHIVED }
enum class FacilitySuggestionStatus { SUGGESTED, UNDER_REVIEW, APPROVED, REJECTED, MERGED }

data class GeoPoint(val latitude: Double, val longitude: Double)

data class Facility(
    val id: UUID,
    val name: String,
    val town: String,
    val address: String?,
    val location: GeoPoint?,
    val status: FacilityStatus,
    val supportedSportIds: List<UUID> = emptyList(),
)

data class FacilitySuggestion(
    val id: UUID,
    val name: String,
    val town: String,
    val address: String?,
    val location: GeoPoint?,
    val suggestedSportIds: List<UUID>,
    val status: FacilitySuggestionStatus = FacilitySuggestionStatus.SUGGESTED,
    val canonicalFacilityId: UUID? = null,
)

data class FacilityQuery(
    val town: String? = null,
    val sportId: UUID? = null,
    val radiusKm: Int? = null,
    val center: GeoPoint? = null,
)
