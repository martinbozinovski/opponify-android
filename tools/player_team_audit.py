from pathlib import Path

root = Path(__file__).resolve().parents[1]
checks = {
    "player models": root / "core/model/src/main/kotlin/com/opponify/model/PlayerModels.kt",
    "team models": root / "core/model/src/main/kotlin/com/opponify/model/TeamModels.kt",
    "profile repository boundary": root / "feature/profile/src/main/kotlin/com/opponify/feature/profile/PlayerProfileRepository.kt",
    "profile ViewModel": root / "feature/profile/src/main/kotlin/com/opponify/feature/profile/PlayerProfileViewModel.kt",
    "team repository boundary": root / "feature/team/src/main/kotlin/com/opponify/feature/team/TeamRepository.kt",
    "team ViewModel": root / "feature/team/src/main/kotlin/com/opponify/feature/team/TeamViewModel.kt",
    "profile Compose": root / "feature/profile/src/main/kotlin/com/opponify/feature/profile/PlayerProfileScreen.kt",
    "team Compose": root / "feature/team/src/main/kotlin/com/opponify/feature/team/TeamScreen.kt",
}
passed = 0
for name, path in checks.items():
    ok = path.exists()
    print(("PASS " if ok else "FAIL ") + name)
    passed += ok
text = "\n".join(p.read_text() for p in checks.values() if p.exists())
for needle, label in [
    ("enum class SkillLevel", "exact skill levels represented"),
    ("enum class TeamRole", "team roles represented"),
    ("enum class MembershipStatus", "membership lifecycle represented"),
    ("idempotencyKey", "critical mutations carry idempotency keys"),
    ("interface PlayerProfileRepository", "profile server boundary"),
    ("interface TeamRepository", "team server boundary"),
]:
    ok = needle in text
    print(("PASS " if ok else "FAIL ") + label)
    passed += ok
total=len(checks)+6
print(f"TOTAL={total} PASS={passed} FAIL={total-passed}")
raise SystemExit(0 if passed==total else 1)
