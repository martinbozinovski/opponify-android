plugins { id("com.android.application"); alias(libs.plugins.compose.compiler); id("com.google.dagger.hilt.android"); id("com.google.devtools.ksp") }
android {
 namespace = "com.opponify.android"
 compileSdk = 36
 defaultConfig {
    applicationId = "com.opponify.android"
    minSdk = 27
    targetSdk = 36
    versionCode = 1
    versionName = "0.1.0"
    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    val apiBase = providers.gradleProperty("OPPONIFY_API_BASE_URL").orNull ?: "http://10.0.2.2:8080/"
    buildConfigField("String", "API_BASE_URL", "\"$apiBase\"")
}
 signingConfigs {
    create("release") {
        val store = System.getenv("OPPONIFY_KEYSTORE_PATH") ?: providers.gradleProperty("OPPONIFY_KEYSTORE_PATH").orNull
            ?: error("OPPONIFY_KEYSTORE_PATH is required for release builds")
        storeFile = file(store)
        storePassword = System.getenv("OPPONIFY_KEYSTORE_PASSWORD") ?: providers.gradleProperty("OPPONIFY_KEYSTORE_PASSWORD").orNull
            ?: error("OPPONIFY_KEYSTORE_PASSWORD is required for release builds")
        keyAlias = System.getenv("OPPONIFY_KEY_ALIAS") ?: providers.gradleProperty("OPPONIFY_KEY_ALIAS").orNull
            ?: error("OPPONIFY_KEY_ALIAS is required for release builds")
        keyPassword = System.getenv("OPPONIFY_KEY_PASSWORD") ?: providers.gradleProperty("OPPONIFY_KEY_PASSWORD").orNull
            ?: error("OPPONIFY_KEY_PASSWORD is required for release builds")
    }
}
buildTypes {
    debug { applicationIdSuffix = ".debug" }
    release {
        signingConfig = signingConfigs.getByName("release")
        isMinifyEnabled = true
        isShrinkResources = true
        proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        val productionApi = providers.gradleProperty("OPPONIFY_PRODUCTION_API_BASE_URL").orNull
            ?: error("OPPONIFY_PRODUCTION_API_BASE_URL is required for production release builds")
        buildConfigField("String", "API_BASE_URL", "\"$productionApi\"")
    }
}
 flavorDimensions += "environment"
 productFlavors { create("dev") { dimension = "environment"; applicationIdSuffix = ".dev" }; create("staging") { dimension = "environment"; applicationIdSuffix = ".staging" }; create("production") { dimension = "environment" } }
 buildFeatures { compose = true; buildConfig = true }
 compileOptions { sourceCompatibility = JavaVersion.VERSION_17; targetCompatibility = JavaVersion.VERSION_17 }
}
dependencies {
 implementation(project(":core:common")); implementation(project(":core:model")); implementation(project(":core:auth")); implementation(project(":core:design-system")); implementation(project(":core:navigation")); implementation(project(":core:network")); implementation(project(":feature:facility")); implementation(project(":feature:discovery")); implementation(project(":feature:opportunity"))
 implementation(libs.androidx.core.ktx); implementation(libs.androidx.activity.compose); implementation(libs.androidx.lifecycle.runtime); implementation(libs.androidx.lifecycle.viewmodel.compose); implementation(libs.androidx.lifecycle.runtime.compose)
 implementation(platform(libs.compose.bom)); implementation(platform(libs.firebase.bom)); implementation(libs.compose.ui); implementation(libs.compose.ui.tooling.preview); implementation(libs.compose.material3); implementation(libs.hilt.android); ksp(libs.hilt.compiler); implementation(libs.firebase.messaging)
 debugImplementation(libs.compose.ui.tooling); androidTestImplementation(platform(libs.compose.bom)); androidTestImplementation(libs.compose.ui.test.junit4); debugImplementation(libs.compose.ui.test.manifest); testImplementation(libs.junit)
}
