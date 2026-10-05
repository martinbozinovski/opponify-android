# Opponify Android — Phase 10F: Participation

Phase 10F implements the Android participation boundary on top of the locked 10A–10E baseline.

Implemented:
- Opportunity request model and lifecycle: Pending, Accepted, Rejected, Withdrawn, Expired
- Individual/team requester distinction
- Participation draft and accepted-participation result models
- Participation repository boundary with idempotent critical mutations
- Request loading, creation, withdrawal, rejection, and acceptance ViewModel operations
- Server-authoritative acceptance result handling
- Compose participation UI
- Unit tests for request/acceptance state handling
- Structural and backward-compatibility audit scripts

Important boundaries:
- An Opportunity Request is not itself a commitment.
- Pending requests do not reserve capacity or time permanently.
- Acceptance is revalidated and authoritative on the backend; the Android client does not manufacture scheduling truth.
- Exact-time versus range/flexible scheduling semantics remain owned by the opportunity/game scheduling domain.
- Team authority remains server-side.
- No waitlist, matchmaking ranking, trust mutation, attendance, result, or booking behavior is implemented here.
- Blocks/capacity/overlap are backend revalidation concerns; stale Android state cannot override them.
- Critical mutations carry idempotency keys.
- Production secrets and Firebase configuration remain externalized.

Validation in this environment:
- Phase 10F structural audit: 11/11 PASS
- Phase 10F compatibility audit: 10/10 PASS
- Local Gradle execution could not run because services.gradle.org is unavailable in this environment.
- GitHub Actions remains the authoritative build gate.
