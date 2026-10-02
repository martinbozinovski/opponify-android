package com.opponify.feature.team

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.opponify.common.architecture.OperationResult
import com.opponify.common.architecture.UiLoadState
import com.opponify.model.Team
import com.opponify.model.TeamMember
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID

sealed interface TeamUiState {
    data object Initial : TeamUiState
    data object Loading : TeamUiState
    data class Content(val team: Team, val members: List<TeamMember>, val loadState: UiLoadState = UiLoadState.Loaded) : TeamUiState
    data class Error(val message: String) : TeamUiState
}

class TeamViewModel(private val repository: TeamRepository) : ViewModel() {
    private val _uiState = MutableStateFlow<TeamUiState>(TeamUiState.Initial)
    val uiState: StateFlow<TeamUiState> = _uiState.asStateFlow()

    fun load(teamId: UUID) {
        _uiState.value = TeamUiState.Loading
        viewModelScope.launch {
            val teamResult = repository.getTeam(teamId)
            val membersResult = repository.getMembers(teamId)
            if (teamResult is OperationResult.Success && membersResult is OperationResult.Success) {
                _uiState.value = TeamUiState.Content(teamResult.value, membersResult.value)
            } else {
                _uiState.value = TeamUiState.Error("Team data could not be loaded.")
            }
        }
    }
}
