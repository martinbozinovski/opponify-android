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
import java.util.UUID

@Composable
fun CommunicationScreen(state: CommunicationUiState, conversationId: UUID?, onLoadConversations: () -> Unit, onLoadMessages: (UUID) -> Unit, onDraftChanged: (String) -> Unit, onSend: (UUID) -> Unit) {
    val content = when (state) { is CommunicationUiState.Content -> state; is CommunicationUiState.Error -> CommunicationUiState.Content(state.conversations, state.messages, state.draft, state.loadState) }
    Column(Modifier.padding(16.dp)) {
        Button(onClick = onLoadConversations) { Text("Load conversations") }
        conversationId?.let { id -> Button(onClick = { onLoadMessages(id) }) { Text("Load messages") } }
        content.messages.forEach { Text(it.body) }
        TextField(value = content.draft.body, onValueChange = onDraftChanged, modifier = Modifier.fillMaxWidth())
        if (conversationId != null) Button(onClick = { onSend(conversationId) }, enabled = content.draft.body.isNotBlank()) { Text("Send") }
    }
}
