package com.opponify.feature.team

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun TeamScreen(state: TeamUiState) {
    Column(Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        when (state) {
            TeamUiState.Initial, TeamUiState.Loading -> Text("Loading team…")
            is TeamUiState.Error -> Text(state.message)
            is TeamUiState.Content -> {
                Text(state.team.name)
                Text("Members: ${state.members.size}")
                state.members.forEach { Text("${it.displayName} — ${it.role}") }
            }
        }
    }
}
