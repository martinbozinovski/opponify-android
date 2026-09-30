from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
checks = {
    "Firebase Auth dependency": "firebase-auth" in (ROOT / "gradle/libs.versions.toml").read_text(),
    "Firebase BoM pinned": 'firebaseBom = "34.19.0"' in (ROOT / "gradle/libs.versions.toml").read_text(),
    "No Firebase KTX dependency": "firebase-auth-ktx" not in (ROOT / "gradle/libs.versions.toml").read_text(),
    "Auth repository boundary": (ROOT / "core/auth/src/main/kotlin/com/opponify/auth/domain/AuthRepository.kt").exists(),
    "Firebase adapter": (ROOT / "core/auth/src/main/kotlin/com/opponify/auth/data/FirebaseAuthRepository.kt").exists(),
    "Auth state": (ROOT / "core/auth/src/main/kotlin/com/opponify/auth/model/AuthState.kt").exists(),
    "ID token boundary": "getIdToken" in (ROOT / "core/auth/src/main/kotlin/com/opponify/auth/domain/AuthRepository.kt").read_text(),
    "Phone verification boundary": "startPhoneVerification" in (ROOT / "core/auth/src/main/kotlin/com/opponify/auth/domain/AuthRepository.kt").read_text(),
    "Email verification": "sendEmailVerification" in (ROOT / "core/auth/src/main/kotlin/com/opponify/auth/domain/AuthRepository.kt").read_text(),
    "Hilt auth module": (ROOT / "core/auth/src/main/kotlin/com/opponify/auth/di/AuthModule.kt").exists(),
    "Auth ViewModel": (ROOT / "core/auth/src/main/kotlin/com/opponify/auth/ui/AuthViewModel.kt").exists(),
    "Firebase config externalized": not any((ROOT / f).exists() for f in ["app/google-services.json", "app/src/dev/google-services.json", "app/src/staging/google-services.json", "app/src/production/google-services.json"]),
    "Built-in Kotlin retained": 'id("org.jetbrains.kotlin.kapt")' not in (ROOT / "core/auth/build.gradle.kts").read_text(),
    "compileSdk 36 retained": 'compileSdk = 36' in (ROOT / "core/auth/build.gradle.kts").read_text(),
    "minSdk 27 retained": 'minSdk = 27' in (ROOT / "core/auth/build.gradle.kts").read_text(),
    "Java 17 retained": 'JavaVersion.VERSION_17' in (ROOT / "core/auth/build.gradle.kts").read_text(),
}
passed = sum(checks.values())
for name, ok in checks.items(): print(("PASS " if ok else "FAIL ") + name)
print(f"TOTAL={len(checks)} PASS={passed} FAIL={len(checks)-passed}")
raise SystemExit(0 if passed == len(checks) else 1)
