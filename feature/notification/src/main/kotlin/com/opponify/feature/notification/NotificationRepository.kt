package com.opponify.feature.notification

import com.opponify.common.architecture.OperationResult
import com.opponify.model.Notification
import com.opponify.model.NotificationPreferences
import java.util.UUID

interface NotificationRepository {
    suspend fun getNotifications(cursor: String? = null): OperationResult<List<Notification>>
    suspend fun markRead(notificationId: UUID, idempotencyKey: String): OperationResult<Notification>
    suspend fun getPreferences(): OperationResult<NotificationPreferences>
    suspend fun updatePreferences(preferences: NotificationPreferences, idempotencyKey: String): OperationResult<NotificationPreferences>
}
