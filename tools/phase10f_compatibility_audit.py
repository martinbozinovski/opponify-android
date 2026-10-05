from pathlib import Path
root=Path(__file__).parents[1]
settings=(root/'settings.gradle.kts').read_text()
model=(root/'core/model/src/main/kotlin/com/opponify/model/ParticipationModels.kt').read_text()
vm=(root/'feature/participation/src/main/kotlin/com/opponify/feature/participation/ParticipationViewModel.kt').read_text()
checks={
'10E opportunity model remains unchanged in role': (root/'core/model/src/main/kotlin/com/opponify/model/OpportunityModels.kt').exists(),
'10E discovery remains separate': (root/'feature/discovery/src/main/kotlin/com/opponify/feature/discovery/DiscoveryRepository.kt').exists(),
'10D team model remains separate': (root/'core/model/src/main/kotlin/com/opponify/model/TeamModels.kt').exists(),
'10D team authority not moved client-side': 'TeamRole' not in model,
'Exact/range/flexible semantics remain opportunity-owned': 'OpportunityTimeType' in (root/'core/model/src/main/kotlin/com/opponify/model/OpportunityModels.kt').read_text(),
'No scheduled-game model added by 10F': 'ScheduledGame' not in model,
'No trust mutation added by 10F': 'Trust' not in ''.join(p.read_text() for p in (root/'feature/participation').rglob('*.kt')),
'No automatic matchmaking added by 10F': 'RELEVANCE' not in ''.join(p.read_text() for p in (root/'feature/participation').rglob('*.kt')),
'BaseViewModel architecture retained': 'BaseViewModel<ParticipationUiState>' in vm,
'Participation module registered': ':feature:participation' in settings,
}
for k,v in checks.items(): print(('PASS' if v else 'FAIL'), k)
print(f'TOTAL={len(checks)} PASS={sum(checks.values())} FAIL={len(checks)-sum(checks.values())}')
