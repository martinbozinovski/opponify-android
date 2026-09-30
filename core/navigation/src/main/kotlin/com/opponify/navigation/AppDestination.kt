package com.opponify.navigation

sealed interface AppDestination {
    val route: String

    data object Root : AppDestination { override val route = "root" }
    data object Discovery : AppDestination { override val route = "discovery" }
    data object Opportunity : AppDestination { override val route = "opportunity" }
    data object Game : AppDestination { override val route = "game" }
    data object Team : AppDestination { override val route = "team" }
    data object Profile : AppDestination { override val route = "profile" }
    data object TrustHistory : AppDestination { override val route = "trust-history" }
    data object Facility : AppDestination { override val route = "facility" }
    data object Notification : AppDestination { override val route = "notification" }
    data object Moderation : AppDestination { override val route = "moderation" }
}
