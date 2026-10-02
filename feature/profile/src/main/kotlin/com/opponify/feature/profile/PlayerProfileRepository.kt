package com.opponify.feature.profile

import com.opponify.common.architecture.OperationResult
import com.opponify.model.PlayerProfile
import com.opponify.model.PlayerProfileDraft
import java.util.UUID

interface PlayerProfileRepository {
    suspend fun getProfile(userId: UUID): OperationResult<PlayerProfile>
    suspend fun updateProfile(userId: UUID, draft: PlayerProfileDraft, idempotencyKey: String): OperationResult<PlayerProfile>
}
