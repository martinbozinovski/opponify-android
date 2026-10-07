package com.opponify.feature.discovery

import com.opponify.common.architecture.AppError
import com.opponify.common.architecture.OperationResult
import com.opponify.model.*
import com.opponify.network.AuthenticatedApiClient
import com.opponify.network.OpponifyApi
import java.time.Instant
import java.util.UUID

class RemoteDiscoveryRepository(client: AuthenticatedApiClient) : DiscoveryRepository {
    private val api: OpponifyApi = client.retrofit.create(OpponifyApi::class.java)
    override suspend fun discover(query: DiscoveryQuery): OperationResult<List<Opportunity>> = try {
        val page=api.discover(query.sportId?.let(::sportName),query.town,50,query.cursor)
        OperationResult.Success(page.items.map { it.toModel() })
    } catch (_: retrofit2.HttpException) { OperationResult.Failure(AppError.Unknown) }
      catch (_: Exception) { OperationResult.Failure(AppError.NetworkUnavailable) }

    private fun sportName(id:UUID):String = when(id){
        UUID.nameUUIDFromBytes("PING_PONG".toByteArray())->"PING_PONG"
        UUID.nameUUIDFromBytes("FUTSAL".toByteArray())->"FUTSAL"
        UUID.nameUUIDFromBytes("STREET_BASKETBALL".toByteArray())->"STREET_BASKETBALL"
        UUID.nameUUIDFromBytes("TENNIS".toByteArray())->"TENNIS"
        else -> "PING_PONG"
    }
    private fun com.opponify.network.OpportunityDto.toModel()=Opportunity(
        UUID.fromString(id),creatorUserId?.let(UUID::fromString),creatorTeamId?.let(UUID::fromString),
        UUID.nameUUIDFromBytes(sport.toByteArray()),OpportunityNeed.valueOf(need),town.orEmpty(),
        OpportunityTimeType.valueOf(timeType),startAt?.let(Instant::parse),endAt?.let(Instant::parse),
        skillLevel?.let(SkillLevel::valueOf),desiredOpponentLevel?.let(SkillLevel::valueOf),facilityId?.let(UUID::fromString),null,null,
        targetCapacity,minimumParticipation,OpportunityStatus.valueOf(status)
    )
}
