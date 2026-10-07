package com.opponify.network

import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.POST
import retrofit2.http.Body
import retrofit2.http.Header
import com.google.gson.JsonObject
import retrofit2.converter.gson.GsonConverterFactory

class AuthenticatedApiClient(
    private val config: NetworkConfig,
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
) {
    private val client = OkHttpClient.Builder()
        .addInterceptor(Interceptor { chain ->
            val token = runBlocking { auth.currentUser?.getIdToken(false)?.await()?.token }
            val request: Request = chain.request().newBuilder().apply {
                if (!token.isNullOrBlank()) header("Authorization", "Bearer $token")
                header("Accept", "application/json")
            }.build()
            chain.proceed(request)
        }).build()

    val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(config.baseUrl.ensureTrailingSlash())
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private fun String.ensureTrailingSlash() = if (endsWith('/')) this else "$this/"
}

interface OpponifyApi {
    @GET("api/v1/opportunities")
    suspend fun discover(
        @Query("sport") sport: String? = null,
        @Query("town") town: String? = null,
        @Query("limit") limit: Int = 50,
        @Query("cursor") cursor: String? = null,
    ): OpportunityPageDto

    @POST("api/v1/opportunities")
    suspend fun createOpportunity(@Body body: JsonObject, @Header("Idempotency-Key") idempotencyKey: String): JsonObject
}

data class OpportunityPageDto(val items: List<OpportunityDto> = emptyList(), val nextCursor: String? = null)
data class OpportunityDto(
    val id: String, val creatorUserId: String?, val creatorTeamId: String?, val sport: String,
    val need: String, val timeType: String, val startAt: String?, val endAt: String?, val town: String?,
    val facilityId: String?, val targetCapacity: Int, val minimumParticipation: Int,
    val skillLevel: String?, val desiredOpponentLevel: String?, val status: String
)
