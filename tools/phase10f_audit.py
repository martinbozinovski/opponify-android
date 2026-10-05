from pathlib import Path
root = Path(__file__).parents[1]
checks = {
    'Participation model': (root/'core/model/src/main/kotlin/com/opponify/model/ParticipationModels.kt').exists(),
    'Request lifecycle': all(x in (root/'core/model/src/main/kotlin/com/opponify/model/ParticipationModels.kt').read_text() for x in ['PENDING','ACCEPTED','REJECTED','WITHDRAWN','EXPIRED']),
    'Individual/team actor distinction': all(x in (root/'core/model/src/main/kotlin/com/opponify/model/ParticipationModels.kt').read_text() for x in ['INDIVIDUAL','TEAM']),
    'Repository boundary': (root/'feature/participation/src/main/kotlin/com/opponify/feature/participation/ParticipationRepository.kt').exists(),
    'Idempotent critical mutations': 'idempotencyKey' in (root/'feature/participation/src/main/kotlin/com/opponify/feature/participation/ParticipationRepository.kt').read_text(),
    'ViewModel state flow': 'BaseViewModel<ParticipationUiState>' in (root/'feature/participation/src/main/kotlin/com/opponify/feature/participation/ParticipationViewModel.kt').read_text(),
    'Server result controls acceptance': 'repository.acceptRequest' in (root/'feature/participation/src/main/kotlin/com/opponify/feature/participation/ParticipationViewModel.kt').read_text(),
    'No client-side scheduling operation': all('createScheduledGame' not in p.read_text() for p in (root/'feature/participation').rglob('*.kt')),
    'No waitlist model': 'Waitlist' not in (root/'core/model/src/main/kotlin/com/opponify/model/ParticipationModels.kt').read_text(),
    'No trust mutation': 'Trust' not in ''.join(p.read_text() for p in (root/'feature/participation').rglob('*.kt')),
    'No matchmaking ranking': 'RELEVANCE' not in ''.join(p.read_text() for p in (root/'feature/participation').rglob('*.kt')),
}
for k,v in checks.items(): print(('PASS' if v else 'FAIL'), k)
print(f'TOTAL={len(checks)} PASS={sum(checks.values())} FAIL={len(checks)-sum(checks.values())}')
