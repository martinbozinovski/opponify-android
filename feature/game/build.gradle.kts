plugins { id("com.android.library") }
android { namespace = "com.opponify.feature.game"; compileSdk = 36; defaultConfig { minSdk = 27 }; compileOptions { sourceCompatibility = JavaVersion.VERSION_17; targetCompatibility = JavaVersion.VERSION_17 } }
dependencies { implementation(project(":core:common")); implementation(project(":core:model")); implementation(project(":core:network")) }
