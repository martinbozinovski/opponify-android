package com.opponify.model

import java.time.Instant
import java.util.UUID

enum class NotificationType {
    PARTICIPATION_REQUEST,
    PARTICIPATION_ACCEPTED,
    PARTICIPATION_REJECTED,
    TIME_PROPOSAL,
    TIME_CONFIRMED,
    GAME_CHANGE,
    GAME_CANCELLED,
    ATTENDANCE_ACTION,
    RESULT_ACTION,
    DISPUTE_ACTION,
    TRUST_UPDATED,
    SYSTEM
}

data class Notification(
    val id: UUID,
    val type: NotificationType,
    val title: String,
    val body: String,
    val createdAt: Instant,
    val readAt: Instant? = null,
    val deepLink: String? = null,
    val entityId: UUID? = null,
)

data class NotificationPreferences(
    val pushEnabled: Boolean = true,
    val inAppEnabled: Boolean = true,
    val criticalWorkflowPushEnabled: Boolean = true,
)

data class Message(
    val id: UUID,
    val conversationId: UUID,
    val senderId: UUID,
    val body: String,
    val createdAt: Instant,
)

data class Conversation(
    val id: UUID,
    val participantIds: List<UUID>,
    val eligible: Boolean = true,
    val lastMessage: Message? = null,
)

data class MessageDraft(val body: String = "")
