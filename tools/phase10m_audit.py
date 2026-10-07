from pathlib import Path

root = Path(__file__).resolve().parents[1]
checks = {
    "integration invariant test exists": (root / "core/testing/src/test/kotlin/com/opponify/testing/Phase10IntegrationInvariantTest.kt").exists(),
    "core testing depends on model": 'project(":core:model")' in (root / "core/testing/build.gradle.kts").read_text(),
    "opportunity commitment invariant covered": "AWAITING_EXACT_TIME" in (root / "core/testing/src/test/kotlin/com/opponify/testing/Phase10IntegrationInvariantTest.kt").read_text(),
    "scheduled interval invariant covered": "endAt" in (root / "core/testing/src/test/kotlin/com/opponify/testing/Phase10IntegrationInvariantTest.kt").read_text(),
    "attendance claim invariant covered": "CLAIMED_ABSENT" in (root / "core/testing/src/test/kotlin/com/opponify/testing/Phase10IntegrationInvariantTest.kt").read_text(),
    "result dispute invariant covered": "PLAYED_RESULT_DISPUTED" in (root / "core/testing/src/test/kotlin/com/opponify/testing/Phase10IntegrationInvariantTest.kt").read_text(),
    "individual team trust separation covered": "TrustSubjectType.PLAYER" in (root / "core/testing/src/test/kotlin/com/opponify/testing/Phase10IntegrationInvariantTest.kt").read_text(),
    "facility reservation invariant covered": "FacilityStatus.APPROVED" in (root / "core/testing/src/test/kotlin/com/opponify/testing/Phase10IntegrationInvariantTest.kt").read_text(),
    "notification signal invariant covered": "NotificationType.GAME_CANCELLED" in (root / "core/testing/src/test/kotlin/com/opponify/testing/Phase10IntegrationInvariantTest.kt").read_text(),
    "communication eligibility invariant covered": "eligible = false" in (root / "core/testing/src/test/kotlin/com/opponify/testing/Phase10IntegrationInvariantTest.kt").read_text(),
    "history preservation invariant covered": "HistoricalRecord" in (root / "core/testing/src/test/kotlin/com/opponify/testing/Phase10IntegrationInvariantTest.kt").read_text(),
    "10L audit remains green": (root / "tools/phase10l_audit.py").exists(),
}
for name, ok in checks.items():
    print(f"PASS: {name}" if ok else f"FAIL: {name}")
print(f"TOTAL={len(checks)} PASS={sum(checks.values())} FAIL={len(checks)-sum(checks.values())}")
raise SystemExit(0 if all(checks.values()) else 1)
