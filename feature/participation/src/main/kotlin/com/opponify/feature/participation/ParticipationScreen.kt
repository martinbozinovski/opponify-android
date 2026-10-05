package com.opponify.feature.participation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import com.opponify.model.OpportunityRequestStatus
import java.util.UUID

@Composable
fun ParticipationScreen(
    viewModel: ParticipationViewModel,
    opportunityId: UUID,
    isCreator: Boolean,
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    when (val current = state) {
        is ParticipationUiState.Content -> ParticipationContent(
            state = current,
            opportunityId = opportunityId,
            isCreator = isCreator,
            onMessageChange = { message -> viewModel.updateDraft { it.copy(message = message) } },
            onCreate = { viewModel.createRequest(opportunityId, UUID.randomUUID().toString()) },
            onWithdraw = { viewModel.withdrawRequest(it, UUID.randomUUID().toString()) },
            onReject = { viewModel.rejectRequest(it, UUID.randomUUID().toString()) },
            onAccept = { viewModel.acceptRequest(it, UUID.randomUUID().toString()) },
        )
        is ParticipationUiState.Error -> Text(
            text = "Participation could not be completed: ${current.error::class.simpleName}",
            modifier = Modifier.padding(24.dp),
            color = MaterialTheme.colorScheme.error,
        )
    }
}

@Composable
private fun ParticipationContent(
    state: ParticipationUiState.Content,
    opportunityId: UUID,
    isCreator: Boolean,
    onMessageChange: (String) -> Unit,
    onCreate: () -> Unit,
    onWithdraw: (UUID) -> Unit,
    onReject: (UUID) -> Unit,
    onAccept: (UUID) -> Unit,
) {
    var message by remember(state.draft.message) { mutableStateOf(state.draft.message) }
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text("Participation", style = MaterialTheme.typography.headlineMedium)
        Text("Opportunity: $opportunityId")
        if (!isCreator) {
            OutlinedTextField(
                value = message,
                onValueChange = { message = it; onMessageChange(it) },
                label = { Text("Optional message") },
                modifier = Modifier.fillMaxWidth(),
            )
            Button(onClick = onCreate, modifier = Modifier.fillMaxWidth()) { Text("Request to join") }
        }
        state.accepted?.let {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("Participation accepted")
                    Text("State: ${it.state}")
                    Text(if (it.scheduledGameId == null) "Scheduling remains server-authoritative." else "Scheduled game created by the server.")
                }
            }
        }
        state.requests.forEach { request ->
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Request ${request.id}")
                    Text("Status: ${request.status}")
                    request.message?.takeIf(String::isNotBlank)?.let { Text(it) }
                    if (isCreator && request.status == OpportunityRequestStatus.PENDING) {
                        Button(onClick = { onAccept(request.id) }) { Text("Accept") }
                        OutlinedButton(onClick = { onReject(request.id) }) { Text("Reject") }
                    }
                    if (!isCreator && request.status == OpportunityRequestStatus.PENDING) {
                        OutlinedButton(onClick = { onWithdraw(request.id) }) { Text("Withdraw") }
                    }
                }
            }
        }
    }
}
