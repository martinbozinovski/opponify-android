plugins { id("com.android.library"); alias(libs.plugins.compose.compiler) }
android { namespace = "com.opponify.feature.opportunity"; compileSdk = 36; defaultConfig { minSdk = 27 }; buildFeatures { compose = true }; compileOptions { sourceCompatibility = JavaVersion.VERSION_17; targetCompatibility = JavaVersion.VERSION_17 } }
dependencies {
    implementation(project(":core:common")); implementation(project(":core:model")); implementation(project(":core:network")); implementation(libs.retrofit); implementation(libs.retrofit.converter.gson); implementation(project(":core:design-system")); implementation(libs.androidx.lifecycle.runtime); implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(platform(libs.compose.bom)); implementation(libs.compose.ui); implementation(libs.compose.material3); implementation(libs.compose.ui.tooling.preview)
    testImplementation(libs.junit); testImplementation(libs.kotlinx.coroutines.test)
}
