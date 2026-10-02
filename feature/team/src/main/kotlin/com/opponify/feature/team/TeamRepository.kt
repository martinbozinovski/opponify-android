package com.opponify.feature.team

import com.opponify.common.architecture.OperationResult
import com.opponify.model.Team
import com.opponify.model.TeamMember
import java.util.UUID

interface TeamRepository {
    suspend fun getTeam(teamId: UUID): OperationResult<Team>
    suspend fun getMembers(teamId: UUID): OperationResult<List<TeamMember>>
    suspend fun requestMembership(teamId: UUID, userId: UUID, idempotencyKey: String): OperationResult<Unit>
    suspend fun leaveTeam(teamId: UUID, userId: UUID, idempotencyKey: String): OperationResult<Unit>
}
