package com.opponify.feature.notification

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.opponify.model.NotificationPreferences
import java.util.UUID

@Composable
fun NotificationScreen(state: NotificationUiState, onLoad: () -> Unit, onMarkRead: (UUID) -> Unit, onPreferences: (NotificationPreferences) -> Unit) {
    val content = when (state) { is NotificationUiState.Content -> state; is NotificationUiState.Error -> NotificationUiState.Content(state.notifications, state.preferences, state.loadState) }
    Column(Modifier.padding(16.dp)) {
        Button(onClick = onLoad) { Text("Load notifications") }
        content.notifications.forEach { notification ->
            Text("${notification.title}: ${notification.body}")
            if (notification.readAt == null) Button(onClick = { onMarkRead(notification.id) }) { Text("Mark read") }
        }
        Button(onClick = { onPreferences(content.preferences.copy(pushEnabled = !content.preferences.pushEnabled)) }) { Text("Push: ${content.preferences.pushEnabled}") }
    }
}
