package com.opponify.model

data class PrivacySettings(
    val analyticsEnabled: Boolean = false,
    val extendedDiagnosticsEnabled: Boolean = false,
    val personalizedNotificationPreviewEnabled: Boolean = false,
)

enum class AccountAnonymizationStatus {
    NOT_REQUESTED,
    REQUESTED,
    PROCESSING,
    COMPLETED,
    FAILED,
}

data class AccountAnonymizationRequest(
    val status: AccountAnonymizationStatus = AccountAnonymizationStatus.NOT_REQUESTED,
)
