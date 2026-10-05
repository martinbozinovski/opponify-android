package com.opponify.feature.notification

import com.opponify.common.architecture.OperationResult
import com.opponify.model.Conversation
import com.opponify.model.Message
import com.opponify.model.MessageDraft
import java.util.UUID

interface CommunicationRepository {
    suspend fun getConversations(): OperationResult<List<Conversation>>
    suspend fun getMessages(conversationId: UUID, cursor: String? = null): OperationResult<List<Message>>
    suspend fun sendMessage(conversationId: UUID, draft: MessageDraft, idempotencyKey: String): OperationResult<Message>
}
