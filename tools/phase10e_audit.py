from pathlib import Path
root=Path(__file__).parents[1]
checks={
'Opportunity model': (root/'core/model/src/main/kotlin/com/opponify/model/OpportunityModels.kt').exists(),
'Opportunity repository boundary': (root/'feature/opportunity/src/main/kotlin/com/opponify/feature/opportunity/OpportunityRepository.kt').exists(),
'Opportunity ViewModel': (root/'feature/opportunity/src/main/kotlin/com/opponify/feature/opportunity/OpportunityViewModel.kt').exists(),
'Opportunity UI': (root/'feature/opportunity/src/main/kotlin/com/opponify/feature/opportunity/OpportunityScreen.kt').exists(),
'Discovery repository boundary': (root/'feature/discovery/src/main/kotlin/com/opponify/feature/discovery/DiscoveryRepository.kt').exists(),
'Discovery ViewModel': (root/'feature/discovery/src/main/kotlin/com/opponify/feature/discovery/DiscoveryViewModel.kt').exists(),
'Discovery UI': (root/'feature/discovery/src/main/kotlin/com/opponify/feature/discovery/DiscoveryScreen.kt').exists(),
'Exact flexible/range/exact time types': 'OpportunityTimeType' in (root/'core/model/src/main/kotlin/com/opponify/model/OpportunityModels.kt').read_text(),
'Need types opponent/players/game': 'OPPONENT' in (root/'core/model/src/main/kotlin/com/opponify/model/OpportunityModels.kt').read_text() and 'PLAYERS' in (root/'core/model/src/main/kotlin/com/opponify/model/OpportunityModels.kt').read_text() and 'GAME' in (root/'core/model/src/main/kotlin/com/opponify/model/OpportunityModels.kt').read_text(),
'Opportunity distinct from Scheduled Game': 'Scheduled' not in (root/'core/model/src/main/kotlin/com/opponify/model/OpportunityModels.kt').read_text(),
'Critical mutations idempotent': 'idempotencyKey' in (root/'feature/opportunity/src/main/kotlin/com/opponify/feature/opportunity/OpportunityRepository.kt').read_text(),
'Discovery explicit sort/query': 'DiscoveryQuery' in (root/'feature/discovery/src/main/kotlin/com/opponify/feature/discovery/DiscoveryRepository.kt').read_text(),
'No hidden matchmaking': 'RELEVANCE' in (root/'core/model/src/main/kotlin/com/opponify/model/OpportunityModels.kt').read_text() and 'DiscoverySort' in (root/'core/model/src/main/kotlin/com/opponify/model/OpportunityModels.kt').read_text(),
}
for k,v in checks.items(): print(('PASS' if v else 'FAIL'), k)
print(f'TOTAL={len(checks)} PASS={sum(checks.values())} FAIL={len(checks)-sum(checks.values())}')
