package com.opponify.feature.notification

import com.opponify.common.architecture.OperationResult
import com.opponify.model.Conversation
import com.opponify.model.Message
import com.opponify.model.MessageDraft
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.Instant
import java.util.UUID

@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
class CommunicationViewModelTest {
    @Test
    fun sendMessageUsesIdempotencyAndClearsDraft() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        try {
            val conversationId = UUID.randomUUID()
            val senderId = UUID.randomUUID()
            var key = ""
            var draftSeen = MessageDraft()
            val message = Message(UUID.randomUUID(), conversationId, senderId, "hello", Instant.EPOCH)
            val repository = object : CommunicationRepository {
                override suspend fun getConversations() = OperationResult.Success(listOf(Conversation(conversationId, listOf(senderId))))
                override suspend fun getMessages(conversationId: UUID, cursor: String?) = OperationResult.Success(emptyList<Message>())
                override suspend fun sendMessage(conversationId: UUID, draft: MessageDraft, idempotencyKey: String): OperationResult<Message> {
                    key = idempotencyKey
                    draftSeen = draft
                    return OperationResult.Success(message)
                }
            }
            val vm = CommunicationViewModel(repository)
            vm.updateDraft("hello")
            vm.sendMessage(conversationId, "idem-10j")
            advanceUntilIdle()
            assertEquals("idem-10j", key)
            assertEquals("hello", draftSeen.body)
            assertEquals("", (vm.uiState.value as CommunicationUiState.Content).draft.body)
        } finally {
            Dispatchers.resetMain()
        }
    }
}
