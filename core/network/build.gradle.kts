plugins { id("com.android.library") }
android { namespace = "com.opponify.network"; compileSdk = 36; defaultConfig { minSdk = 27 }; compileOptions { sourceCompatibility = JavaVersion.VERSION_17; targetCompatibility = JavaVersion.VERSION_17 } }
dependencies { implementation(project(":core:common")); implementation(libs.retrofit); implementation(libs.okhttp) }
