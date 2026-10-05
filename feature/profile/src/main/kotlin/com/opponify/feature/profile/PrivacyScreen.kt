package com.opponify.feature.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp

@Composable
fun PrivacyScreen(
    state: PrivacyUiState,
    onAnalyticsChanged: (Boolean) -> Unit,
    onDiagnosticsChanged: (Boolean) -> Unit,
    onNotificationPreviewChanged: (Boolean) -> Unit,
    onSave: () -> Unit,
    onClearLocalData: () -> Unit,
    onRequestAnonymization: () -> Unit,
) {
    val content = when (state) {
        is PrivacyUiState.Content -> state
        is PrivacyUiState.Error -> PrivacyUiState.Content(state.settings, state.anonymization, state.loadState)
    }
    Column(
        Modifier.padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(stringResource(R.string.privacy_title))
        Text(stringResource(R.string.privacy_description))
        PrivacyToggle(
            label = stringResource(R.string.analytics_label),
            checked = content.settings.analyticsEnabled,
            onCheckedChange = onAnalyticsChanged,
        )
        PrivacyToggle(
            label = stringResource(R.string.diagnostics_label),
            checked = content.settings.extendedDiagnosticsEnabled,
            onCheckedChange = onDiagnosticsChanged,
        )
        PrivacyToggle(
            label = stringResource(R.string.notification_preview_label),
            checked = content.settings.personalizedNotificationPreviewEnabled,
            onCheckedChange = onNotificationPreviewChanged,
        )
        Button(onClick = onSave, modifier = Modifier.fillMaxWidth()) {
            Text(stringResource(R.string.save_privacy))
        }
        Button(onClick = onClearLocalData, modifier = Modifier.fillMaxWidth()) {
            Text(stringResource(R.string.clear_local_data))
        }
        Button(onClick = onRequestAnonymization, modifier = Modifier.fillMaxWidth()) {
            Text(stringResource(R.string.request_anonymization))
        }
        Text(stringResource(R.string.anonymization_status, content.anonymization.status.name))
    }
}

@Composable
private fun PrivacyToggle(label: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        Modifier.fillMaxWidth().semantics { contentDescription = label },
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(label, Modifier.weight(1f))
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}
