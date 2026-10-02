package com.opponify.feature.opportunity

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun OpportunityScreen(
    state: OpportunityUiState,
    onTownChanged: (String) -> Unit,
    onInfoChanged: (String) -> Unit,
    onSave: () -> Unit,
) {
    when (state) {
        is OpportunityUiState.Form -> OpportunityForm(state, onTownChanged, onInfoChanged, onSave)
        is OpportunityUiState.Content -> OpportunityForm(state, onTownChanged, onInfoChanged, onSave)
        is OpportunityUiState.Error -> Text("Unable to load opportunity.", Modifier.padding(16.dp))
    }
}

@Composable
private fun OpportunityForm(
    state: OpportunityUiState,
    onTownChanged: (String) -> Unit,
    onInfoChanged: (String) -> Unit,
    onSave: () -> Unit,
) {
    Column(Modifier.padding(16.dp)) {
        TextField(state.draft.town, onTownChanged, Modifier.fillMaxWidth(), label = { Text("Town") })
        TextField(state.draft.additionalInfo, onInfoChanged, Modifier.fillMaxWidth(), label = { Text("Additional information") })
        Button(onClick = onSave, enabled = state.loadState != com.opponify.common.architecture.UiLoadState.Loading, modifier = Modifier.padding(top = 12.dp)) {
            Text("Save opportunity")
        }
    }
}
