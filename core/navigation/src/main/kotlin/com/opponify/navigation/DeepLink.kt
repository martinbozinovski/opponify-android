package com.opponify.navigation

data class AppDeepLink(
    val destination: AppDestination,
    val stableId: String? = null,
)
