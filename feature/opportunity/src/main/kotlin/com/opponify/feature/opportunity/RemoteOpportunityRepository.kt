package com.opponify.feature.opportunity

import com.google.gson.JsonObject
import com.opponify.common.architecture.AppError
import com.opponify.common.architecture.OperationResult
import com.opponify.model.*
import com.opponify.network.AuthenticatedApiClient
import com.opponify.network.OpponifyApi
import java.time.Instant
import java.util.UUID

class RemoteOpportunityRepository(client: AuthenticatedApiClient) : OpportunityRepository {
    private val api=client.retrofit.create(OpponifyApi::class.java)
    override suspend fun createOpportunity(draft:OpportunityDraft,idempotencyKey:String):OperationResult<Opportunity> = try {
        val body=JsonObject().apply {
            addProperty("creatorTeamId",draft.creatorTeamId?.toString()); addProperty("sport",sportName(draft.sportId));
            addProperty("need",draft.need.name); addProperty("timeType",draft.timeType.name);
            addProperty("startAt",draft.startAt?.toString()); addProperty("endAt",draft.endAt?.toString());
            addProperty("town",draft.town); addProperty("facilityId",draft.facilityId?.toString());
            addProperty("targetCapacity",draft.targetCapacity); addProperty("minimumParticipation",draft.minimumRequired);
            addProperty("skillLevel",draft.level?.name); addProperty("desiredOpponentLevel",draft.desiredOpponentLevel?.name)
        }
        OperationResult.Success(api.createOpportunity(body,idempotencyKey).toModel())
    } catch (_:Exception){ OperationResult.Failure(AppError.NetworkUnavailable) }
    override suspend fun getOpportunity(id:UUID)=OperationResult.Failure(AppError.NotFound)
    override suspend fun updateOpportunity(id:UUID,draft:OpportunityDraft,idempotencyKey:String)=OperationResult.Failure(AppError.Unknown)
    override suspend fun reopenOpportunity(id:UUID,idempotencyKey:String)=OperationResult.Failure(AppError.Unknown)
    override suspend fun cancelOpportunity(id:UUID,idempotencyKey:String)=OperationResult.Failure(AppError.Unknown)
    private fun com.google.gson.JsonObject.toModel() = Opportunity(
        UUID.fromString(get("id").asString),
        get("creatorUserId")?.takeUnless { it.isJsonNull }?.asString?.let(UUID::fromString),
        get("creatorTeamId")?.takeUnless { it.isJsonNull }?.asString?.let(UUID::fromString),
        UUID.nameUUIDFromBytes(get("sport").asString.toByteArray()),
        OpportunityNeed.valueOf(get("need").asString), get("town")?.takeUnless { it.isJsonNull }?.asString.orEmpty(),
        OpportunityTimeType.valueOf(get("timeType").asString),
        get("startAt")?.takeUnless { it.isJsonNull }?.asString?.let(Instant::parse),
        get("endAt")?.takeUnless { it.isJsonNull }?.asString?.let(Instant::parse),
        get("skillLevel")?.takeUnless { it.isJsonNull }?.asString?.let(SkillLevel::valueOf),
        get("desiredOpponentLevel")?.takeUnless { it.isJsonNull }?.asString?.let(SkillLevel::valueOf),
        get("facilityId")?.takeUnless { it.isJsonNull }?.asString?.let(UUID::fromString), null, null,
        get("targetCapacity").asInt, get("minimumParticipation").asInt, OpportunityStatus.valueOf(get("status").asString)
    )
    private fun sportName(id:UUID?)=when(id){
        UUID.nameUUIDFromBytes("PING_PONG".toByteArray())->"PING_PONG"
        UUID.nameUUIDFromBytes("FUTSAL".toByteArray())->"FUTSAL"
        UUID.nameUUIDFromBytes("STREET_BASKETBALL".toByteArray())->"STREET_BASKETBALL"
        UUID.nameUUIDFromBytes("TENNIS".toByteArray())->"TENNIS"
        else->"PING_PONG"
    }
}
