from pathlib import Path

root = Path(__file__).resolve().parents[1]
checks = {
    "privacy models exist": (root / "core/model/src/main/kotlin/com/opponify/model/PrivacyModels.kt").exists(),
    "keystore encrypted local store exists": (root / "core/database/src/main/kotlin/com/opponify/database/EncryptedLocalStore.kt").exists(),
    "accessibility semantics exist": (root / "core/design-system/src/main/kotlin/com/opponify/designsystem/AccessibilitySemantics.kt").exists(),
    "privacy repository boundary exists": (root / "feature/profile/src/main/kotlin/com/opponify/feature/profile/PrivacyRepository.kt").exists(),
    "privacy view model uses BaseViewModel": "BaseViewModel<PrivacyUiState>" in (root / "feature/profile/src/main/kotlin/com/opponify/feature/profile/PrivacyViewModel.kt").read_text(),
    "privacy screen localized": "stringResource" in (root / "feature/profile/src/main/kotlin/com/opponify/feature/profile/PrivacyScreen.kt").read_text(),
    "English resources": (root / "feature/profile/src/main/res/values/strings.xml").exists(),
    "Macedonian resources": (root / "feature/profile/src/main/res/values-mk/strings.xml").exists(),
    "locale config": (root / "app/src/main/res/xml/locales_config.xml").exists(),
    "no trust mutation": "Trust" not in (root / "feature/profile/src/main/kotlin/com/opponify/feature/profile/PrivacyViewModel.kt").read_text(),
    "anonymization is repository operation": "requestAccountAnonymization" in (root / "feature/profile/src/main/kotlin/com/opponify/feature/profile/PrivacyRepository.kt").read_text(),
    "privacy tests": (root / "feature/profile/src/test/kotlin/com/opponify/feature/profile/PrivacyViewModelTest.kt").exists(),
}
for name, ok in checks.items():
    print(f"PASS: {name}" if ok else f"FAIL: {name}")
print(f"TOTAL={len(checks)} PASS={sum(checks.values())} FAIL={len(checks)-sum(checks.values())}")
raise SystemExit(0 if all(checks.values()) else 1)
