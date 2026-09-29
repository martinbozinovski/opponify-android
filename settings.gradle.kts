import org.gradle.api.initialization.resolve.RepositoriesMode

pluginManagement { repositories { google(); mavenCentral(); gradlePluginPortal() } }
dependencyResolutionManagement { repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS); repositories { google(); mavenCentral() } }
rootProject.name = "Opponify"
include(":app")
include(":core:common")
include(":core:network")
include(":core:database")
include(":core:auth")
include(":core:model")
include(":core:navigation")
include(":core:design-system")
include(":core:testing")
include(":feature:discovery")
include(":feature:opportunity")
include(":feature:game")
include(":feature:team")
include(":feature:profile")
include(":feature:trust-history")
include(":feature:facility")
include(":feature:notification")
include(":feature:moderation")
