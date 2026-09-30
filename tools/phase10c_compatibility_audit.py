from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
checks = {
    "10A compileSdk unchanged": 'compileSdk = 36' in (ROOT/'app/build.gradle.kts').read_text(),
    "10A minSdk unchanged": 'minSdk = 27' in (ROOT/'app/build.gradle.kts').read_text(),
    "10A targetSdk unchanged": 'targetSdk = 36' in (ROOT/'app/build.gradle.kts').read_text(),
    "10A Java 17 unchanged": 'JavaVersion.VERSION_17' in (ROOT/'app/build.gradle.kts').read_text(),
    "10A built-in Kotlin unchanged": 'org.jetbrains.kotlin.kapt' not in '\n'.join(p.read_text() for p in ROOT.rglob('*.gradle.kts')), 
    "10A KSP retained": 'com.google.devtools.ksp' in (ROOT/'build.gradle.kts').read_text(),
    "10A Firebase secrets externalized": not (ROOT/'app/google-services.json').exists(),
    "Phase 7 identity separated from domain User": 'Firebase Authentication is the identity provider' in (ROOT/'core/auth/README.md').read_text(),
    "Phase 7B ID token boundary": 'getIdToken' in (ROOT/'core/auth/src/main/kotlin/com/opponify/auth/domain/AuthRepository.kt').read_text(),
    "Phase 7B email verification boundary": 'sendEmailVerification' in (ROOT/'core/auth/src/main/kotlin/com/opponify/auth/domain/AuthRepository.kt').read_text(),
    "Phase 7B phone verification boundary": 'startPhoneVerification' in (ROOT/'core/auth/src/main/kotlin/com/opponify/auth/domain/AuthRepository.kt').read_text(),
    "Phase 8 local state not authoritative": 'FirebaseAuth.AuthStateListener' in (ROOT/'core/auth/src/main/kotlin/com/opponify/auth/data/FirebaseAuthRepository.kt').read_text(),
    "Phase 8 explicit auth states": all(x in (ROOT/'core/auth/src/main/kotlin/com/opponify/auth/model/AuthState.kt').read_text() for x in ['Loading','SignedOut','SignedIn']),
    "Phase 8 critical auth actions server/provider confirmed": 'await()' in (ROOT/'core/auth/src/main/kotlin/com/opponify/auth/data/FirebaseAuthRepository.kt').read_text(),
    "Phase 9 backend token source compatible": 'getIdToken(forceRefresh)' in (ROOT/'core/auth/src/main/kotlin/com/opponify/auth/data/FirebaseAuthRepository.kt').read_text(),
}
passed=sum(checks.values())
for name, ok in checks.items(): print(("PASS " if ok else "FAIL ")+name)
print(f"TOTAL={len(checks)} PASS={passed} FAIL={len(checks)-passed}")
raise SystemExit(0 if passed==len(checks) else 1)
