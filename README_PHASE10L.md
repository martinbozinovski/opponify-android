# Opponify Phase 10L — Privacy, Accessibility & Localization

This package is the complete Phase 10L merge candidate, based on the Phase 10K project tree.

## Merge

Use this package as the project tree for the Phase 10L CI run, or copy the Phase 10L-added files into the current Phase 10K tree.

### Added files
- `core/model/src/main/kotlin/com/opponify/model/PrivacyModels.kt`
- `core/database/src/main/kotlin/com/opponify/database/EncryptedLocalStore.kt`
- `core/design-system/src/main/kotlin/com/opponify/designsystem/AccessibilitySemantics.kt`
- `feature/profile/src/main/kotlin/com/opponify/feature/profile/PrivacyRepository.kt`
- `feature/profile/src/main/kotlin/com/opponify/feature/profile/PrivacyViewModel.kt`
- `feature/profile/src/main/kotlin/com/opponify/feature/profile/PrivacyScreen.kt`
- `feature/profile/src/main/res/values/strings.xml`
- `feature/profile/src/main/res/values-mk/strings.xml`
- `feature/profile/src/test/kotlin/com/opponify/feature/profile/PrivacyViewModelTest.kt`
- `app/src/main/res/xml/locales_config.xml`
- `app/src/main/res/values/strings.xml`
- `app/src/main/res/values-mk/strings.xml`
- `tools/phase10l_audit.py`

### Modified file
- `app/src/main/AndroidManifest.xml`: declares the English/Macedonian locale configuration.

## Validation

The focused Phase 10L static audit reports 12/12 PASS.

A local Gradle test was attempted but could not start because this environment cannot resolve `services.gradle.org`; authoritative CI is required.

## Locked-baseline constraints preserved
- No trust mutation in 10L.
- Account anonymization remains an explicit repository operation; the Android client does not rewrite historical truth.
- Local data clearing is explicit.
- Encrypted local storage uses an Android Keystore-backed AES/GCM key.
- Accessibility semantics are added without changing domain state.
- English and Macedonian resources are explicit.
- No facility, scheduling, participation, attendance, result, or communication semantics are changed.
