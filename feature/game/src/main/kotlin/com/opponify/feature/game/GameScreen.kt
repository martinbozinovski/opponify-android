package com.opponify.feature.game

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import java.time.Duration
import java.time.Instant
import java.util.UUID

@Composable
fun GameScreen(viewModel: GameViewModel, opportunityId: UUID? = null) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    Column(Modifier.fillMaxSize().padding(24.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("Scheduling & Game", style = MaterialTheme.typography.headlineMedium)
        when (val current = state) {
            is GameUiState.Content -> GameContent(current, viewModel, opportunityId)
            is GameUiState.Error -> Text("Game operation failed: ${current.error::class.simpleName}", color = MaterialTheme.colorScheme.error)
        }
    }
}

@Composable
private fun GameContent(state: GameUiState.Content, viewModel: GameViewModel, opportunityId: UUID?) {
    var startText by remember(state.draft.startAt) { mutableStateOf(state.draft.startAt?.toString().orEmpty()) }
    var minutesText by remember(state.draft.duration) { mutableStateOf(state.draft.duration?.toMinutes()?.toString().orEmpty()) }
    OutlinedTextField(startText, { startText = it }, label = { Text("Exact start (ISO-8601 UTC)") }, modifier = Modifier.fillMaxWidth())
    OutlinedTextField(minutesText, { minutesText = it }, label = { Text("Duration (minutes)") }, modifier = Modifier.fillMaxWidth())
    Button(onClick = {
        viewModel.updateDraft { it.copy(startAt = runCatching { Instant.parse(startText) }.getOrNull(), duration = minutesText.toLongOrNull()?.let(Duration::ofMinutes)) }
        opportunityId?.let { viewModel.schedule(it, UUID.randomUUID().toString()) }
    }, enabled = opportunityId != null, modifier = Modifier.fillMaxWidth()) { Text("Schedule") }
    state.game?.let { game ->
        Card(Modifier.fillMaxWidth()) { Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text("Game: ${game.id}"); Text("Status: ${game.status}"); Text("Start: ${game.startAt}"); Text("End: ${game.endAt}"); Text("Timezone: ${game.timeZone}")
            Text("The backend remains authoritative for commitment and scheduling conflicts.")
        }}
    }
}
