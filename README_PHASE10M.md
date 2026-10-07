# Opponify — Phase 10M: Android Integration & Invariant Validation

## Merge / CI

This package is based on the Phase 10L CI candidate and adds only Phase 10M validation work.

1. Merge/copy the package contents into the existing Android project.
2. Run the normal GitHub Actions Android CI.
3. The authoritative gate remains the full Gradle CI build/test.

## Added in 10M

- `core/testing/src/test/kotlin/com/opponify/testing/Phase10IntegrationInvariantTest.kt`
  - Cross-feature invariant tests covering:
    - opportunity acceptance vs commitment
    - authoritative scheduled-game interval
    - attendance claims vs confirmed facts
    - disputed result vs confirmed result
    - player/team trust separation
    - facility semantics
    - notification signal semantics
    - communication eligibility
    - historical preservation
- `tools/phase10m_audit.py`
- `tools/phase10l_backward_compatibility_audit.py`

## Scope protection

10M does not introduce new product decisions, matchmaking, booking, trust calculation, or backend authority changes. It validates integration assumptions across the already-locked phases.

## Local validation limitation

The available environment cannot resolve `services.gradle.org`, so local Gradle execution cannot be treated as authoritative. GitHub Actions CI remains the build gate.
