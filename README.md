# Opponify Android — Phase 10D

Phase 10D establishes the Android Player & Team feature boundary on top of the locked 10A–10C baseline.

Implemented:
- Player profile and editable draft models
- Exact Easy/Medium/Hard skill model
- Player profile repository boundary with idempotent update operation
- Player profile ViewModel and Compose screen
- Team, membership, member, role and lifecycle models
- Captain/Manager/Member role representation
- Team repository boundary with idempotent membership mutations
- Team ViewModel and Compose screen
- Unit tests for profile and team state handling
- Structural and backward-compatibility audit scripts

Important boundaries:
- Firebase identity remains separate from Opponify Player Profile/User.
- Team is separate from individual player identity and history.
- Repository interfaces represent server-authoritative boundaries; this phase does not invent local authoritative domain state.
- Team authority remains server-side.
- No opportunity, scheduled-game, attendance, result, trust, moderation or booking behavior is implemented here.
- Production secrets and Firebase configuration remain externalized.

Local Gradle execution is unavailable in this environment because services.gradle.org cannot be resolved. CI remains the authoritative build gate.
