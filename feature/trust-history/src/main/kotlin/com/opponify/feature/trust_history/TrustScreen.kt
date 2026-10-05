package com.opponify.feature.trust_history

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.opponify.model.DisputeStatus
import com.opponify.model.TrustAssessment

@Composable
fun TrustScreen(
    assessment: TrustAssessment?,
    disputes: List<com.opponify.model.Dispute>,
    resolutionNote: String,
    onResolutionNoteChanged: (String) -> Unit,
    onResolveDispute: (com.opponify.model.Dispute) -> Unit,
) {
    LazyColumn(
        modifier = Modifier.padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item {
            Text("Trust", style = MaterialTheme.typography.headlineMedium)
            assessment?.let {
                Text("${it.category.displayName()}${it.score?.let { score -> " — $score" } ?: ""}")
                Text("Confirmed games: ${it.evidenceSummary.confirmedGames}")
                Text("Completed without result: ${it.evidenceSummary.completedWithoutResult}")
                Text("Confirmed no-shows: ${it.evidenceSummary.confirmedNoShows}")
                Text("Late cancellations: ${it.evidenceSummary.lateCancellations}")
                if (it.isUpdating || it.isStale) Text("Trust assessment is updating; the last valid assessment is shown.")
            }
        }
        items(disputes) { dispute ->
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("${dispute.type} — ${dispute.status}")
                if (dispute.status != DisputeStatus.RESOLVED) {
                    OutlinedTextField(
                        value = resolutionNote,
                        onValueChange = onResolutionNoteChanged,
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Resolution note") },
                    )
                    Button(onClick = { onResolveDispute(dispute) }) { Text("Resolve dispute") }
                }
            }
        }
    }
}

private fun com.opponify.model.TrustCategory.displayName(): String = when (this) {
    com.opponify.model.TrustCategory.NEW_PROVISIONAL -> "New / Provisional"
    com.opponify.model.TrustCategory.NEEDS_IMPROVEMENT -> "Needs Improvement"
    com.opponify.model.TrustCategory.RELIABLE -> "Reliable"
    com.opponify.model.TrustCategory.VERY_RELIABLE -> "Very Reliable"
    com.opponify.model.TrustCategory.EXCELLENT -> "Excellent"
}
