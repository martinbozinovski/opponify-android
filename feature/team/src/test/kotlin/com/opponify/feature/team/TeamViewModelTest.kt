package com.opponify.feature.team

import com.opponify.common.architecture.OperationResult
import com.opponify.model.*
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.UUID

private class FakeTeamRepository : TeamRepository {
    private val teamId = UUID.randomUUID()
    override suspend fun getTeam(teamId: UUID) = OperationResult.Success(Team(teamId, "Test Team", UUID.randomUUID(), SkillLevel.MEDIUM, "Skopje", true))
    override suspend fun getMembers(teamId: UUID) = OperationResult.Success(listOf(TeamMember(UUID.randomUUID(), "Captain", TeamRole.CAPTAIN, MembershipStatus.ACTIVE)))
    override suspend fun requestMembership(teamId: UUID, userId: UUID, idempotencyKey: String) = OperationResult.Success(Unit)
    override suspend fun leaveTeam(teamId: UUID, userId: UUID, idempotencyKey: String) = OperationResult.Success(Unit)
}

class TeamViewModelTest {
    @Test
    fun load_keeps_team_and_members_separate() = runTest {
        val vm = TeamViewModel(FakeTeamRepository())
        vm.load(UUID.randomUUID())
        advanceUntilIdle()
        val state = vm.uiState.value as TeamUiState.Content
        assertEquals("Test Team", state.team.name)
        assertEquals(1, state.members.size)
        assertEquals(TeamRole.CAPTAIN, state.members.first().role)
    }
}
