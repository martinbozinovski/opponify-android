from pathlib import Path

root = Path(__file__).resolve().parents[1]

def read(rel):
    p = root / rel
    return p.read_text() if p.exists() else ""

def exists(rel):
    return (root / rel).exists()

checks = {
    # Build/project integrity
    "Gradle wrapper present": exists("gradlew"),
    "settings present": exists("settings.gradle.kts"),
    "version catalog present": exists("gradle/libs.versions.toml"),
    "CI workflow present": exists(".github/workflows/android.yml"),
    "10M integration test present": exists("core/testing/src/test/kotlin/com/opponify/testing/Phase10IntegrationInvariantTest.kt"),

    # Locked architecture
    "BaseViewModel present": any((root / "core").glob("**/BaseViewModel.kt")),
    "UiLoadState present": any("sealed interface UiLoadState" in p.read_text() for p in (root / "core").glob("**/*.kt")),
    "Hilt present": "hilt" in read("gradle/libs.versions.toml").lower(),
    "Room present": "room" in read("gradle/libs.versions.toml").lower(),
    "Retrofit present": "retrofit" in read("gradle/libs.versions.toml").lower(),

    # Product/domain invariant regression coverage
    "opportunity vs commitment covered": "AWAITING_EXACT_TIME" in read("core/testing/src/test/kotlin/com/opponify/testing/Phase10IntegrationInvariantTest.kt"),
    "authoritative game interval covered": "endAt" in read("core/testing/src/test/kotlin/com/opponify/testing/Phase10IntegrationInvariantTest.kt"),
    "attendance claims covered": "CLAIMED_ABSENT" in read("core/testing/src/test/kotlin/com/opponify/testing/Phase10IntegrationInvariantTest.kt"),
    "result dispute covered": "PLAYED_RESULT_DISPUTED" in read("core/testing/src/test/kotlin/com/opponify/testing/Phase10IntegrationInvariantTest.kt"),
    "trust separation covered": "TrustSubjectType.PLAYER" in read("core/testing/src/test/kotlin/com/opponify/testing/Phase10IntegrationInvariantTest.kt"),
    "facility semantics covered": "FacilityStatus.APPROVED" in read("core/testing/src/test/kotlin/com/opponify/testing/Phase10IntegrationInvariantTest.kt"),
    "notification signal covered": "NotificationType.GAME_CANCELLED" in read("core/testing/src/test/kotlin/com/opponify/testing/Phase10IntegrationInvariantTest.kt"),
    "communication eligibility covered": "eligible = false" in read("core/testing/src/test/kotlin/com/opponify/testing/Phase10IntegrationInvariantTest.kt"),
    "history preservation covered": "HistoricalRecord" in read("core/testing/src/test/kotlin/com/opponify/testing/Phase10IntegrationInvariantTest.kt"),

    # Phase audit chain remains available
    "10A foundation audit present": exists("tools/foundation_audit.py"),
    "10B architecture audit present": exists("tools/architecture_audit.py"),
    "10C compatibility audit present": exists("tools/phase10c_compatibility_audit.py"),
    "10D compatibility audit present": exists("tools/phase10d_compatibility_audit.py"),
    "10E audit present": exists("tools/phase10e_audit.py"),
    "10F audit present": exists("tools/phase10f_audit.py"),
    "10G audit present": exists("tools/phase10g_audit.py"),
    "10H audit present": exists("tools/phase10h_audit.py"),
    "10I audit present": exists("tools/phase10i_audit.py"),
    "10L audit present": exists("tools/phase10l_audit.py"),
    "10L compatibility audit present": exists("tools/phase10l_backward_compatibility_audit.py"),
    "10M audit present": exists("tools/phase10m_audit.py"),

    # Privacy/localization/accessibility artifacts
    "English resources present": any((root / "feature").glob("**/values/strings.xml")) or any((root / "core").glob("**/values/strings.xml")),
    "Macedonian resources present": any((root / "feature").glob("**/values-mk/strings.xml")) or any((root / "core").glob("**/values-mk/strings.xml")),
    "privacy implementation present": any((root / "feature").glob("**/*Privacy*.kt")),
    "facility implementation present": any((root / "feature").glob("**/*Facility*.kt")),
    "notification implementation present": any((root / "feature").glob("**/*Notification*.kt")),
}

failed = [name for name, ok in checks.items() if not ok]
for name, ok in checks.items():
    print(("PASS" if ok else "FAIL") + ": " + name)
print(f"TOTAL={len(checks)} PASS={len(checks)-len(failed)} FAIL={len(failed)}")
if failed:
    raise SystemExit(1)
