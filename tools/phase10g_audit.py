from pathlib import Path
root=Path(__file__).resolve().parents[1]
checks={
'ScheduledGame model': (root/'core/model/src/main/kotlin/com/opponify/model/GameModels.kt').exists(),
'authoritative exact start/duration/end': all(x in (root/'core/model/src/main/kotlin/com/opponify/model/GameModels.kt').read_text() for x in ['startAt: Instant','duration: Duration','endAt: Instant']),
'TimeProposal first class': 'data class TimeProposal' in (root/'core/model/src/main/kotlin/com/opponify/model/GameModels.kt').read_text(),
'Material GameChange first class': 'data class GameChange' in (root/'core/model/src/main/kotlin/com/opponify/model/GameModels.kt').read_text(),
'critical mutations idempotent': 'idempotencyKey: String' in (root/'feature/game/src/main/kotlin/com/opponify/feature/game/GameRepository.kt').read_text(),
'backend repository boundary': (root/'feature/game/src/main/kotlin/com/opponify/feature/game/GameRepository.kt').exists(),
'ViewModel state flow': 'BaseViewModel<GameUiState>' in (root/'feature/game/src/main/kotlin/com/opponify/feature/game/GameViewModel.kt').read_text(),
'no attendance/results': not any(w in ''.join(p.read_text() for p in (root/'feature/game/src').rglob('*.kt')) for w in ['Attendance','ResultStatus']),
'no automatic matchmaking': 'matchmak' not in ''.join(p.read_text().lower() for p in (root/'feature/game/src').rglob('*.kt')),
'no booking/payment': not any(w in ''.join(p.read_text().lower() for p in (root/'feature/game/src').rglob('*.kt')) for w in ['booking','payment','reservation']),
'game tests': (root/'feature/game/src/test/kotlin/com/opponify/feature/game/GameViewModelTest.kt').exists(),
}
for k,v in checks.items(): print(('PASS' if v else 'FAIL'),k)
print(f'TOTAL={len(checks)} PASS={sum(checks.values())} FAIL={len(checks)-sum(checks.values())}')
