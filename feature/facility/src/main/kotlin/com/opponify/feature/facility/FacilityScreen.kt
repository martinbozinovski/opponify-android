package com.opponify.feature.facility

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun FacilityScreen(
    state: FacilityUiState,
    onDiscover: () -> Unit,
    onUseCurrentLocation: () -> Unit,
    onSelect: (java.util.UUID) -> Unit,
) {
    val content = when (state) {
        is FacilityUiState.Content -> state
        is FacilityUiState.Error -> FacilityUiState.Content(state.facilities, state.selected, state.query, state.location, state.loadState, state.suggestion)
    }
    Column(Modifier.padding(16.dp)) {
        Button(onClick = onUseCurrentLocation) { Text("Use current location") }
        Button(onClick = onDiscover) { Text("Find facilities") }
        Text("Location: ${content.location}")
        content.facilities.forEach { facility ->
            Button(onClick = { onSelect(facility.id) }) { Text(facility.name) }
            Text("${facility.town}${facility.address?.let { ", $it" } ?: ""}")
        }
        content.selected?.let { selected ->
            Text("Selected: ${selected.name}")
            Text("Facility information is not a reservation or availability guarantee.")
        }
    }
}
