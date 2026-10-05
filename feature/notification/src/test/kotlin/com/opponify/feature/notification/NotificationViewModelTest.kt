package com.opponify.feature.notification

import com.opponify.common.architecture.OperationResult
import com.opponify.model.Notification
import com.opponify.model.NotificationPreferences
import com.opponify.model.NotificationType
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
class NotificationViewModelTest {
    @Test
    fun markReadReplacesNotificationFromServerResult() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        try {
            val id = UUID.randomUUID()
            val read = Notification(id, NotificationType.SYSTEM, "t", "b", Instant.EPOCH, Instant.EPOCH)
            val repository = object : NotificationRepository {
                override suspend fun getNotifications(cursor: String?) = OperationResult.Success(listOf(read.copy(readAt = null)))
                override suspend fun markRead(notificationId: UUID, idempotencyKey: String) = OperationResult.Success(read)
                override suspend fun getPreferences() = OperationResult.Success(NotificationPreferences())
                override suspend fun updatePreferences(preferences: NotificationPreferences, idempotencyKey: String) = OperationResult.Success(preferences)
            }
            val vm = NotificationViewModel(repository)
            vm.load()
            advanceUntilIdle()
            vm.markRead(id, "idem-10j")
            advanceUntilIdle()
            assertEquals(Instant.EPOCH, (vm.uiState.value as NotificationUiState.Content).notifications.single().readAt)
        } finally {
            Dispatchers.resetMain()
        }
    }
}
