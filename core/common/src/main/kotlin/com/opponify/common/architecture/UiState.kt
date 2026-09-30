package com.opponify.common.architecture

sealed interface UiLoadState {
    data object Initial : UiLoadState
    data object Loading : UiLoadState
    data object Loaded : UiLoadState
    data object Refreshing : UiLoadState
    data object Stale : UiLoadState
    data object Offline : UiLoadState
    data class Error(val error: AppError) : UiLoadState
}

data class UiMessage(
    val id: String,
    val messageKey: String,
)
