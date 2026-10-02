package com.opponify.feature.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.Button
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun PlayerProfileScreen(state: PlayerProfileUiState, onDisplayNameChanged: (String) -> Unit, onTownChanged: (String) -> Unit, onBioChanged: (String) -> Unit, onSave: () -> Unit) {
    val draft = when (state) { is PlayerProfileUiState.Content -> state.draft; is PlayerProfileUiState.Empty -> state.draft; is PlayerProfileUiState.Error -> return }
    Column(Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("Player profile")
        OutlinedTextField(draft.displayName, onDisplayNameChanged, label = { Text("Display name") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(draft.town, onTownChanged, label = { Text("Town") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(draft.bio, onBioChanged, label = { Text("About you") }, modifier = Modifier.fillMaxWidth())
        Button(onClick = onSave) { Text("Save profile") }
    }
}
