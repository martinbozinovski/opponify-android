plugins { id("com.android.application"); alias(libs.plugins.compose.compiler); id("com.google.dagger.hilt.android"); id("com.google.devtools.ksp") }
android {
 namespace = "com.opponify.android"
 compileSdk = 36
 defaultConfig { applicationId = "com.opponify.android"; minSdk = 27; targetSdk = 36; versionCode = 1; versionName = "0.1.0"; testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner" }
 buildTypes { release { isMinifyEnabled = false } }
 flavorDimensions += "environment"
 productFlavors { create("dev") { dimension = "environment"; applicationIdSuffix = ".dev" }; create("staging") { dimension = "environment"; applicationIdSuffix = ".staging" }; create("production") { dimension = "environment" } }
 buildFeatures { compose = true }
 compileOptions { sourceCompatibility = JavaVersion.VERSION_17; targetCompatibility = JavaVersion.VERSION_17 }
}
dependencies {
 implementation(project(":core:common")); implementation(project(":core:model")); implementation(project(":core:auth")); implementation(project(":core:design-system")); implementation(project(":core:navigation"))
 implementation(libs.androidx.core.ktx); implementation(libs.androidx.activity.compose); implementation(libs.androidx.lifecycle.runtime); implementation(libs.androidx.lifecycle.viewmodel.compose); implementation(libs.androidx.lifecycle.runtime.compose)
 implementation(platform(libs.compose.bom)); implementation(libs.compose.ui); implementation(libs.compose.ui.tooling.preview); implementation(libs.compose.material3); implementation(libs.hilt.android); ksp(libs.hilt.compiler)
 debugImplementation(libs.compose.ui.tooling); androidTestImplementation(platform(libs.compose.bom)); androidTestImplementation(libs.compose.ui.test.junit4); debugImplementation(libs.compose.ui.test.manifest); testImplementation(libs.junit)
}
