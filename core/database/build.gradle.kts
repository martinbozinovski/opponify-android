plugins { id("com.android.library") }
android { namespace = "com.opponify.database"; compileSdk = 36; defaultConfig { minSdk = 27 }; compileOptions { sourceCompatibility = JavaVersion.VERSION_17; targetCompatibility = JavaVersion.VERSION_17 } }
dependencies { implementation(libs.androidx.room.runtime); implementation(libs.androidx.room.ktx); testImplementation(libs.androidx.room.testing) }
