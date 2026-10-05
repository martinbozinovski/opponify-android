package com.opponify.feature.profile

import com.opponify.common.architecture.OperationResult
import com.opponify.model.AccountAnonymizationRequest
import com.opponify.model.PrivacySettings

interface PrivacyRepository {
    suspend fun loadSettings(): OperationResult<PrivacySettings>
    suspend fun updateSettings(settings: PrivacySettings, idempotencyKey: String): OperationResult<PrivacySettings>
    suspend fun clearLocalData(): OperationResult<Unit>
    suspend fun requestAccountAnonymization(idempotencyKey: String): OperationResult<AccountAnonymizationRequest>
}
