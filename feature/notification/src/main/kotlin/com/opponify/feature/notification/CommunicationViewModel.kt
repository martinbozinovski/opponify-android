package com.opponify.feature.notification

import androidx.lifecycle.viewModelScope
import com.opponify.common.architecture.AppError
import com.opponify.common.architecture.BaseViewModel
import com.opponify.common.architecture.OperationResult
import com.opponify.common.architecture.UiLoadState
import com.opponify.model.Conversation
import com.opponify.model.Message
import com.opponify.model.MessageDraft
import kotlinx.coroutines.launch
import java.util.UUID

sealed interface CommunicationUiState {
    val conversations: List<Conversation>
    val messages: List<Message>
    val draft: MessageDraft
    val loadState: UiLoadState

    data class Content(
        override val conversations: List<Conversation> = emptyList(),
        override val messages: List<Message> = emptyList(),
        override val draft: MessageDraft = MessageDraft(),
        override val loadState: UiLoadState = UiLoadState.Initial,
    ) : CommunicationUiState

    data class Error(
        val error: AppError,
        override val conversations: List<Conversation> = emptyList(),
        override val messages: List<Message> = emptyList(),
        override val draft: MessageDraft = MessageDraft(),
        override val loadState: UiLoadState = UiLoadState.Error(error),
    ) : CommunicationUiState
}

class CommunicationViewModel(private val repository: CommunicationRepository) : BaseViewModel<CommunicationUiState>(CommunicationUiState.Content()) {
    fun loadConversations() {
        updateState { it.content().copy(loadState = UiLoadState.Loading) }
        viewModelScope.launch {
            when (val result = repository.getConversations()) {
                is OperationResult.Success -> updateState { it.content().copy(conversations = result.value, loadState = UiLoadState.Loaded) }
                is OperationResult.Failure -> fail(result.error)
            }
        }
    }

    fun loadMessages(conversationId: UUID) = viewModelScope.launch {
        updateState { it.content().copy(loadState = UiLoadState.Loading) }
        when (val result = repository.getMessages(conversationId)) {
            is OperationResult.Success -> updateState { it.content().copy(messages = result.value, loadState = UiLoadState.Loaded) }
            is OperationResult.Failure -> fail(result.error)
        }
    }

    fun updateDraft(body: String) = updateState { it.content().copy(draft = MessageDraft(body)) }

    fun sendMessage(conversationId: UUID, idempotencyKey: String) = viewModelScope.launch {
        when (val result = repository.sendMessage(conversationId, uiState.value.content().draft, idempotencyKey)) {
            is OperationResult.Success -> updateState { it.content().copy(messages = it.content().messages + result.value, draft = MessageDraft()) }
            is OperationResult.Failure -> fail(result.error)
        }
    }

    private fun fail(error: AppError) = updateState { c -> val s = c.content(); CommunicationUiState.Error(error, s.conversations, s.messages, s.draft) }
    private fun CommunicationUiState.content() = when (this) {
        is CommunicationUiState.Content -> this
        is CommunicationUiState.Error -> CommunicationUiState.Content(conversations, messages, draft, loadState)
    }
}
