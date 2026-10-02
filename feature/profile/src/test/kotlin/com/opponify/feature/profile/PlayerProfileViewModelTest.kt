package com.opponify.feature.profile

import com.opponify.common.architecture.OperationResult
import com.opponify.model.PlayerProfile
import com.opponify.model.PlayerProfileDraft
import com.opponify.model.SkillLevel
import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.UUID
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest

private class FakePlayerProfileRepository : PlayerProfileRepository {
    val id = UUID.randomUUID()
    override suspend fun getProfile(userId: UUID) = OperationResult.Success(PlayerProfile(id, "Martin", SkillLevel.MEDIUM, SkillLevel.EASY, "Skopje", "Bio"))
    override suspend fun updateProfile(userId: UUID, draft: PlayerProfileDraft, idempotencyKey: String) = OperationResult.Success(PlayerProfile(id, draft.displayName, draft.skillLevel, draft.desiredOpponentLevel, draft.town, draft.bio))
}

class PlayerProfileViewModelTest {
    @Test
    fun load_exposes_server_profile_and_editable_draft() = runTest {
        val vm = PlayerProfileViewModel(FakePlayerProfileRepository())
        vm.load(UUID.randomUUID())
        advanceUntilIdle()
        val state = vm.uiState.value as PlayerProfileUiState.Content
        assertEquals("Martin", state.profile.displayName)
        assertEquals("Skopje", state.draft.town)
    }
}
