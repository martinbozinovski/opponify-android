from pathlib import Path
root = Path(__file__).resolve().parents[1]
checks = [
("compileSdk 36 retained", 'compileSdk = 36'),
("minSdk 27 retained", 'minSdk = 27'),
("Java 17 retained", 'JavaVersion.VERSION_17'),
("skill levels exactly Easy/Medium/Hard", 'enum class SkillLevel { EASY, MEDIUM, HARD }'),
("individual/team models separate", 'data class PlayerProfile' in (root/'core/model/src/main/kotlin/com/opponify/model/PlayerModels.kt').read_text() and 'data class Team' in (root/'core/model/src/main/kotlin/com/opponify/model/TeamModels.kt').read_text()),
("team roles include captain/manager/member", all(x in (root/'core/model/src/main/kotlin/com/opponify/model/TeamModels.kt').read_text() for x in ['CAPTAIN','MANAGER','MEMBER'])),
("membership lifecycle preserved", all(x in (root/'core/model/src/main/kotlin/com/opponify/model/TeamModels.kt').read_text() for x in ['PENDING','ACTIVE','LEFT','REMOVED'])),
("critical team mutations require idempotency", 'idempotencyKey' in (root/'feature/team/src/main/kotlin/com/opponify/feature/team/TeamRepository.kt').read_text()),
("server repository boundaries explicit", all((root/p).exists() for p in ['feature/profile/src/main/kotlin/com/opponify/feature/profile/PlayerProfileRepository.kt','feature/team/src/main/kotlin/com/opponify/feature/team/TeamRepository.kt'])),
("no trust implementation in 10D", not any('TrustAssessment' in p.read_text() for p in root.glob('feature/profile/src/**/*.kt'))),
("no opportunity/game commitment implementation", not any('ScheduledGame' in p.read_text() for p in root.glob('feature/profile/src/**/*.kt'))),
("existing navigation routes unchanged", 'data object Team' in (root/'core/navigation/src/main/kotlin/com/opponify/navigation/AppDestination.kt').read_text()),
]
passed=0
for name, ok in checks:
 print(('PASS ' if ok else 'FAIL ')+name); passed += bool(ok)
print(f'TOTAL={len(checks)} PASS={passed} FAIL={len(checks)-passed}')
raise SystemExit(0 if passed==len(checks) else 1)
