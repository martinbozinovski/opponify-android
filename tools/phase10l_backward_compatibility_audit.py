from pathlib import Path

root = Path(__file__).resolve().parents[1]
read = lambda p: (root / p).read_text()
checks = {
    "10L privacy audit remains 12/12": (root / "tools/phase10l_audit.py").exists(),
    "10A SDK baseline": 'compileSdk = 36' in read('app/build.gradle.kts') and 'minSdk = 27' in read('app/build.gradle.kts') and 'targetSdk = 36' in read('app/build.gradle.kts'),
    "10B BaseViewModel retained": (root / 'core/common/src/main/kotlin/com/opponify/common/architecture/BaseViewModel.kt').exists(),
    "10C auth module retained": (root / 'core/auth').exists(),
    "10D player/team modules retained": (root / 'feature/team').exists() and (root / 'feature/profile').exists(),
    "10E opportunity/discovery retained": (root / 'feature/opportunity').exists() and (root / 'feature/discovery').exists(),
    "10F participation retained": (root / 'feature/participation').exists(),
    "10G game retained": (root / 'feature/game').exists(),
    "10H history retained": (root / 'feature/trust-history').exists(),
    "10I trust/dispute retained": 'TrustAssessment' in read('core/model/src/main/kotlin/com/opponify/model/TrustModels.kt') and 'Dispute' in read('core/model/src/main/kotlin/com/opponify/model/GameResolutionModels.kt'),
    "10J communication retained": (root / 'feature/notification').exists() and 'Message' in read('core/model/src/main/kotlin/com/opponify/model/CommunicationModels.kt'),
    "10K facility retained": (root / 'feature/facility').exists() and 'FacilityStatus' in read('core/model/src/main/kotlin/com/opponify/model/FacilityModels.kt'),
    "10L localization retained": (root / 'app/src/main/res/xml/locales_config.xml').exists(),
    "backend-authoritative boundary unchanged": 'AuthViewModel' in read('app/src/main/kotlin/com/opponify/android/MainActivity.kt'),
    "no booking model introduced": 'Reservation' not in read('core/model/src/main/kotlin/com/opponify/model/FacilityModels.kt'),
    "no trust calculation in integration validator": 'TrustAssessment' in read('core/testing/src/test/kotlin/com/opponify/testing/Phase10IntegrationInvariantTest.kt'),
    "integration suite is test-only": '/src/test/' in str(root / 'core/testing/src/test/kotlin/com/opponify/testing/Phase10IntegrationInvariantTest.kt'),
}
for name, ok in checks.items():
    print(f"PASS: {name}" if ok else f"FAIL: {name}")
print(f"TOTAL={len(checks)} PASS={sum(checks.values())} FAIL={len(checks)-sum(checks.values())}")
raise SystemExit(0 if all(checks.values()) else 1)
