plugins { id("com.android.library") }
android { namespace = "com.opponify.common"; compileSdk = 36; defaultConfig { minSdk = 27 }; compileOptions { sourceCompatibility = JavaVersion.VERSION_17; targetCompatibility = JavaVersion.VERSION_17 } }
dependencies { implementation(libs.kotlinx.coroutines.android) }
