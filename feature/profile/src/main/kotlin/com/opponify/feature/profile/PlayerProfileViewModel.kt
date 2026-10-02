package com.opponify.feature.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.opponify.common.architecture.OperationResult
import com.opponify.common.architecture.UiLoadState
import com.opponify.model.PlayerProfile
import com.opponify.model.PlayerProfileDraft
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID

sealed interface PlayerProfileUiState {
    val loadState: UiLoadState
    data class Content(val profile: PlayerProfile, val draft: PlayerProfileDraft, override val loadState: UiLoadState = UiLoadState.Loaded) : PlayerProfileUiState
    data class Empty(val draft: PlayerProfileDraft = PlayerProfileDraft(), override val loadState: UiLoadState = UiLoadState.Initial) : PlayerProfileUiState
    data class Error(val message: String, override val loadState: UiLoadState = UiLoadState.Error(com.opponify.common.architecture.AppError.Unknown)) : PlayerProfileUiState
}

class PlayerProfileViewModel(private val repository: PlayerProfileRepository) : ViewModel() {
    private val _uiState = MutableStateFlow<PlayerProfileUiState>(PlayerProfileUiState.Empty())
    val uiState: StateFlow<PlayerProfileUiState> = _uiState.asStateFlow()

    fun load(userId: UUID) {
        _uiState.value = when (val current = _uiState.value) {
            is PlayerProfileUiState.Content -> current.copy(loadState = UiLoadState.Loading)
            else -> PlayerProfileUiState.Empty(loadState = UiLoadState.Loading)
        }
        viewModelScope.launch {
            when (val result = repository.getProfile(userId)) {
                is OperationResult.Success -> _uiState.value = PlayerProfileUiState.Content(result.value, result.value.toDraft())
                is OperationResult.Failure -> _uiState.value = PlayerProfileUiState.Error(result.error::class.simpleName ?: "Unknown error")
            }
        }
    }

    fun updateDraft(transform: (PlayerProfileDraft) -> PlayerProfileDraft) {
        _uiState.value = when (val current = _uiState.value) {
            is PlayerProfileUiState.Content -> current.copy(draft = transform(current.draft))
            is PlayerProfileUiState.Empty -> current.copy(draft = transform(current.draft))
            is PlayerProfileUiState.Error -> current
        }
    }

    fun save(userId: UUID, idempotencyKey: String) {
        val current = _uiState.value as? PlayerProfileUiState.Content ?: return
        _uiState.value = current.copy(loadState = UiLoadState.Loading)
        viewModelScope.launch {
            when (val result = repository.updateProfile(userId, current.draft, idempotencyKey)) {
                is OperationResult.Success -> _uiState.value = PlayerProfileUiState.Content(result.value, result.value.toDraft())
                is OperationResult.Failure -> _uiState.value = current.copy(loadState = UiLoadState.Error(result.error))
            }
        }
    }

    private fun PlayerProfile.toDraft() = PlayerProfileDraft(displayName, skillLevel, desiredOpponentLevel, town.orEmpty(), bio.orEmpty())
}
