package com.opponify.feature.discovery

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun DiscoveryScreen(state: DiscoveryUiState) {
    when (state) {
        DiscoveryUiState.Initial -> Text("Find a game or opportunity.", Modifier.padding(16.dp))
        is DiscoveryUiState.Content -> Column(Modifier.padding(16.dp)) {
            Text("Opportunities: ${state.opportunities.size}")
            state.opportunities.forEach { Text("${it.town} · ${it.need}") }
        }
        is DiscoveryUiState.Error -> Text("Unable to load opportunities.", Modifier.padding(16.dp))
    }
}
