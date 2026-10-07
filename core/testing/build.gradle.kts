plugins { id("com.android.library") }
android { namespace = "com.opponify.testing"; compileSdk = 36; defaultConfig { minSdk = 27 }; compileOptions { sourceCompatibility = JavaVersion.VERSION_17; targetCompatibility = JavaVersion.VERSION_17 } }
dependencies {
    implementation(project(":core:model"))
    implementation(libs.junit)
    implementation(libs.kotlinx.coroutines.android)
    testImplementation(libs.kotlinx.coroutines.test)
}
