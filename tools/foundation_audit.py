from pathlib import Path
r=Path(__file__).resolve().parents[1]
checks={
"settings":(r/"settings.gradle.kts").exists(),
"catalog":(r/"gradle/libs.versions.toml").exists(),
"AGP 9.4.0":'id("com.android.application") version "9.4.0"' in (r/"build.gradle.kts").read_text(),
"built-in Kotlin":'org.jetbrains.kotlin.android' not in '\n'.join(p.read_text() for p in r.rglob('*.gradle.kts')),
"Compose compiler":'org.jetbrains.kotlin.plugin.compose' in (r/"build.gradle.kts").read_text(),
"SDKs":all(x in (r/"app/build.gradle.kts").read_text() for x in ['compileSdk = 36','minSdk = 27','targetSdk = 36']),
"JDK17":'JavaVersion.VERSION_17' in (r/"app/build.gradle.kts").read_text(),
"environments":all(x in (r/"app/build.gradle.kts").read_text() for x in ['create("dev")','create("staging")','create("production")']),
"Room":'libs.androidx.room.runtime' in (r/"core/database/build.gradle.kts").read_text(),
"Retrofit/OkHttp":all(x in (r/"core/network/build.gradle.kts").read_text() for x in ['libs.retrofit','libs.okhttp']),
"Hilt":'com.google.dagger.hilt.android' in (r/"build.gradle.kts").read_text(),
"design system":(r/"core/design-system/src/main/kotlin/com/opponify/design-system/Theme.kt").exists(),
"features":all((r/"feature"/x).exists() for x in ['discovery','opportunity','game','team','profile','trust-history','facility','notification','moderation']),
"CI":(r/".github/workflows/android.yml").exists(),
"no signing files":not any(p.suffix in {'.jks','.keystore'} for p in r.rglob('*') if p.is_file()),
}
for k,v in checks.items(): print(('PASS ' if v else 'FAIL ')+k)
print(f'TOTAL={len(checks)} PASS={sum(checks.values())} FAIL={len(checks)-sum(checks.values())}')
raise SystemExit(0 if all(checks.values()) else 1)
