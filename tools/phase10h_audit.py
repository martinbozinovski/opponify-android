from pathlib import Path
root=Path(__file__).resolve().parents[1]
model=(root/'core/model/src/main/kotlin/com/opponify/model/GameResolutionModels.kt').read_text()
repo=(root/'feature/trust-history/src/main/kotlin/com/opponify/feature/trust_history/HistoryRepository.kt').read_text()
vm=(root/'feature/trust-history/src/main/kotlin/com/opponify/feature/trust_history/HistoryViewModel.kt').read_text()
hist_files=[p for p in (root/'feature/trust-history/src').rglob('*.kt') if p.name.startswith('History')]
allsrc=''.join(p.read_text() for p in hist_files)
trust_main=''.join(p.read_text() for p in (root/'feature/trust-history/src/main').rglob('Trust*.kt'))
checks={
'attendance states': all(x in model for x in ['EXPECTED','CLAIMED_ATTENDED','CLAIMED_ABSENT','CONFIRMED_ATTENDED','CONFIRMED_ABSENT','DISPUTED','UNRESOLVED']),
'result lifecycle': all(x in model for x in ['PLAYED_NO_RESULT','PLAYED_RESULT_SUBMITTED','PLAYED_RESULT_CONFIRMED','PLAYED_RESULT_DISPUTED','NOT_PLAYED']),
'authoritative history model': all(x in model for x in ['data class HistoricalRecord','attendance: List<AttendanceEvent>','result: Result?','disputes: List<Dispute>']),
'repository boundary': all(x in repo for x in ['getHistory','submitAttendance','submitResult','openDispute']),
'critical writes idempotent': all(x in repo for x in ['idempotencyKey: String']),
'ViewModel state flow': 'BaseViewModel<HistoryUiState>' in vm,
'dispute first class': 'data class Dispute' in model,
'no trust mutation': not any(x in allsrc for x in ['TrustScore','TrustAssessment','TrustEvidence','recalculateTrust']),
'trust screens read-only (no recalculation/override)': not any(x in trust_main for x in ['recalculateTrust','setTrust','updateTrust','overrideTrust','adjustTrust']),
'no automatic outcome': not any(x in allsrc for x in ['autoConfirm','automaticNoShow','AUTO_PLAYED']),
'tests present': (root/'feature/trust-history/src/test/kotlin/com/opponify/feature/trust_history/HistoryViewModelTest.kt').exists(),
}
for k,v in checks.items(): print(('PASS' if v else 'FAIL'),k)
print(f'TOTAL={len(checks)} PASS={sum(checks.values())} FAIL={len(checks)-sum(checks.values())}')
