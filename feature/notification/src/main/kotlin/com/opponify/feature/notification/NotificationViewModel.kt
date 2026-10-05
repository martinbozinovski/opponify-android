package com.opponify.feature.notification

import androidx.lifecycle.viewModelScope
import com.opponify.common.architecture.AppError
import com.opponify.common.architecture.BaseViewModel
import com.opponify.common.architecture.OperationResult
import com.opponify.common.architecture.UiLoadState
import com.opponify.model.Notification
import com.opponify.model.NotificationPreferences
import kotlinx.coroutines.launch
import java.util.UUID

sealed interface NotificationUiState {
    val notifications: List<Notification>
    val preferences: NotificationPreferences
    val loadState: UiLoadState

    data class Content(
        override val notifications: List<Notification> = emptyList(),
        override val preferences: NotificationPreferences = NotificationPreferences(),
        override val loadState: UiLoadState = UiLoadState.Initial,
    ) : NotificationUiState

    data class Error(
        val error: AppError,
        override val notifications: List<Notification> = emptyList(),
        override val preferences: NotificationPreferences = NotificationPreferences(),
        override val loadState: UiLoadState = UiLoadState.Error(error),
    ) : NotificationUiState
}

class NotificationViewModel(private val repository: NotificationRepository) : BaseViewModel<NotificationUiState>(NotificationUiState.Content()) {
    fun load() {
        updateState { it.content().copy(loadState = UiLoadState.Loading) }
        viewModelScope.launch {
            when (val result = repository.getNotifications()) {
                is OperationResult.Success -> updateState { it.content().copy(notifications = result.value, loadState = UiLoadState.Loaded) }
                is OperationResult.Failure -> fail(result.error)
            }
        }
    }

    fun markRead(notificationId: UUID, idempotencyKey: String) = viewModelScope.launch {
        when (val result = repository.markRead(notificationId, idempotencyKey)) {
            is OperationResult.Success -> updateState { state -> state.content().copy(notifications = state.content().notifications.map { if (it.id == notificationId) result.value else it }) }
            is OperationResult.Failure -> fail(result.error)
        }
    }

    fun loadPreferences() = viewModelScope.launch {
        when (val result = repository.getPreferences()) {
            is OperationResult.Success -> updateState { it.content().copy(preferences = result.value) }
            is OperationResult.Failure -> fail(result.error)
        }
    }

    fun updatePreferences(preferences: NotificationPreferences, idempotencyKey: String) = viewModelScope.launch {
        when (val result = repository.updatePreferences(preferences, idempotencyKey)) {
            is OperationResult.Success -> updateState { it.content().copy(preferences = result.value) }
            is OperationResult.Failure -> fail(result.error)
        }
    }

    private fun fail(error: AppError) = updateState { c -> val s = c.content(); NotificationUiState.Error(error, s.notifications, s.preferences) }
    private fun NotificationUiState.content() = when (this) {
        is NotificationUiState.Content -> this
        is NotificationUiState.Error -> NotificationUiState.Content(notifications, preferences, loadState)
    }
}
