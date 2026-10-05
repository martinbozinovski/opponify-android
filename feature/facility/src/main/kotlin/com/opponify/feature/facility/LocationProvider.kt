package com.opponify.feature.facility

import com.opponify.model.GeoPoint
import kotlinx.coroutines.flow.StateFlow

interface LocationProvider {
    val state: StateFlow<LocationState>
    suspend fun refresh()
}

sealed interface LocationState {
    data object Unavailable : LocationState
    data object PermissionRequired : LocationState
    data object Loading : LocationState
    data class Available(val point: GeoPoint) : LocationState
    data class Error(val message: String) : LocationState
}
