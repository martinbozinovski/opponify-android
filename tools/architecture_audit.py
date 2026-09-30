from pathlib import Path

root = Path(__file__).resolve().parents[1]
checks = []

def check(name, condition):
    checks.append((name, bool(condition)))

catalog = (root / "gradle/libs.versions.toml").read_text()
app_gradle = (root / "app/build.gradle.kts").read_text()
manifest = (root / "app/src/main/AndroidManifest.xml").read_text()
common_gradle = (root / "core/common/build.gradle.kts").read_text()

check("base ViewModel primitive", (root / "core/common/src/main/kotlin/com/opponify/common/architecture/BaseViewModel.kt").exists())
check("immutable StateFlow exposure", "StateFlow<S>" in (root / "core/common/src/main/kotlin/com/opponify/common/architecture/BaseViewModel.kt").read_text())
check("typed application errors", (root / "core/common/src/main/kotlin/com/opponify/common/architecture/AppError.kt").exists())
check("operation result abstraction", (root / "core/common/src/main/kotlin/com/opponify/common/architecture/OperationResult.kt").exists())
check("navigation destinations", (root / "core/navigation/src/main/kotlin/com/opponify/navigation/AppDestination.kt").exists())
check("stable deep-link model", (root / "core/navigation/src/main/kotlin/com/opponify/navigation/DeepLink.kt").exists())
check("local data policy", (root / "core/database/src/main/kotlin/com/opponify/database/LocalDataPolicy.kt").exists())
check("network environment boundary", (root / "core/network/src/main/kotlin/com/opponify/network/NetworkEnvironment.kt").exists())
check("network mutation headers", (root / "core/network/src/main/kotlin/com/opponify/network/NetworkHeaders.kt").exists())
check("Hilt application", "@HiltAndroidApp" in (root / "app/src/main/kotlin/com/opponify/android/OpponifyApplication.kt").read_text())
check("Hilt compiler via KSP", "hilt-compiler" in catalog and "ksp(libs.hilt.compiler)" in app_gradle and "org.jetbrains.kotlin.kapt" not in app_gradle)
check("Hilt module", "@InstallIn(SingletonComponent::class)" in (root / "app/src/main/kotlin/com/opponify/android/di/AppModule.kt").read_text())
check("Hilt manifest application", 'android:name=".OpponifyApplication"' in manifest)
check("Lifecycle StateFlow dependencies", "androidx-lifecycle-viewmodel-compose" in catalog and "androidx-lifecycle-runtime-compose" in catalog)
check("common tests", (root / "core/common/src/test/kotlin/com/opponify/common/architecture/BaseViewModelTest.kt").exists())
check("Built-in Kotlin retained", "org.jetbrains.kotlin.kapt" not in (root / "build.gradle.kts").read_text() and "org.jetbrains.kotlin.kapt" not in app_gradle)
check("Java 17 retained", "JavaVersion.VERSION_17" in common_gradle and "JavaVersion.VERSION_17" in app_gradle)

passed = sum(ok for _, ok in checks)
for name, ok in checks:
    print(f"{'PASS' if ok else 'FAIL'} {name}")
print(f"TOTAL={len(checks)} PASS={passed} FAIL={len(checks)-passed}")
raise SystemExit(0 if passed == len(checks) else 1)
