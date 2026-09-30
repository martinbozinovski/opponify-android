package com.opponify.network

enum class NetworkEnvironment {
    DEV,
    STAGING,
    PRODUCTION,
}

data class NetworkConfig(
    val environment: NetworkEnvironment,
    val baseUrl: String,
)
