from pathlib import Path
root=Path(__file__).resolve().parents[1]
model=(root/'core/model/src/main/kotlin/com/opponify/model/TrustModels.kt').read_text()
repo=(root/'feature/trust-history/src/main/kotlin/com/opponify/feature/trust_history/TrustRepository.kt').read_text()
vm=(root/'feature/trust-history/src/main/kotlin/com/opponify/feature/trust_history/TrustViewModel.kt').read_text()
test=(root/'feature/trust-history/src/test/kotlin/com/opponify/feature/trust_history/TrustViewModelTest.kt').read_text()
allsrc=''.join(p.read_text() for p in (root/'feature/trust-history/src').rglob('*.kt'))
checks={
'five trust categories': all(x in model for x in ['NEW_PROVISIONAL','NEEDS_IMPROVEMENT','RELIABLE','VERY_RELIABLE','EXCELLENT']),
'individual/team trust subject separation': all(x in model for x in ['TrustSubjectType','PLAYER','TEAM']),
'evidence summary is derived representation': 'TrustEvidenceSummary' in model,
'methodology version present': 'methodologyVersion: String' in model,
'stale/update state present': all(x in model for x in ['isUpdating','isStale']),
'repository boundary': all(x in repo for x in ['getTrustAssessment','getDisputes','resolveDispute']),
'critical dispute resolution idempotent': 'idempotencyKey: String' in repo,
'ViewModel uses BaseViewModel': 'BaseViewModel<TrustUiState>' in vm,
'dispute resolution preserves history object': 'result.value' in vm and 'map { if (it.id == disputeId)' in vm,
'no direct trust calculation in client': not any(x in allsrc for x in ['calculateTrust(', 'recalculateTrust(', 'TrustScoreCalculator']),
'tests present': 'class TrustViewModelTest' in test,
}
for k,v in checks.items(): print(('PASS' if v else 'FAIL'),k)
print(f'TOTAL={len(checks)} PASS={sum(checks.values())} FAIL={len(checks)-sum(checks.values())}')
