plugins { id("com.android.library") }
android { namespace = "com.opponify.designsystem"; compileSdk = 36; defaultConfig { minSdk = 27 }; compileOptions { sourceCompatibility = JavaVersion.VERSION_17; targetCompatibility = JavaVersion.VERSION_17 } }
plugins { alias(libs.plugins.compose.compiler) }
android { buildFeatures { compose = true } }
dependencies { implementation(platform(libs.compose.bom)); implementation(libs.compose.ui); implementation(libs.compose.material3); implementation(libs.compose.ui.tooling.preview) }
