package com.opponify.feature.profile

import androidx.lifecycle.viewModelScope
import com.opponify.common.architecture.AppError
import com.opponify.common.architecture.BaseViewModel
import com.opponify.common.architecture.OperationResult
import com.opponify.common.architecture.UiLoadState
import com.opponify.model.AccountAnonymizationRequest
import com.opponify.model.PrivacySettings
import kotlinx.coroutines.launch

sealed interface PrivacyUiState {
    val settings: PrivacySettings
    val anonymization: AccountAnonymizationRequest
    val loadState: UiLoadState

    data class Content(
        override val settings: PrivacySettings = PrivacySettings(),
        override val anonymization: AccountAnonymizationRequest = AccountAnonymizationRequest(),
        override val loadState: UiLoadState = UiLoadState.Initial,
    ) : PrivacyUiState

    data class Error(
        val error: AppError,
        override val settings: PrivacySettings = PrivacySettings(),
        override val anonymization: AccountAnonymizationRequest = AccountAnonymizationRequest(),
        override val loadState: UiLoadState = UiLoadState.Error(error),
    ) : PrivacyUiState
}

class PrivacyViewModel(
    private val repository: PrivacyRepository,
) : BaseViewModel<PrivacyUiState>(PrivacyUiState.Content()) {

    fun load() = viewModelScope.launch {
        updateState { it.content().copy(loadState = UiLoadState.Loading) }
        when (val result = repository.loadSettings()) {
            is OperationResult.Success -> updateState { it.content().copy(settings = result.value, loadState = UiLoadState.Loaded) }
            is OperationResult.Failure -> fail(result.error)
        }
    }

    fun setAnalyticsEnabled(enabled: Boolean) = updateSettings { it.copy(analyticsEnabled = enabled) }

    fun setExtendedDiagnosticsEnabled(enabled: Boolean) = updateSettings { it.copy(extendedDiagnosticsEnabled = enabled) }

    fun setPersonalizedNotificationPreviewEnabled(enabled: Boolean) =
        updateSettings { it.copy(personalizedNotificationPreviewEnabled = enabled) }

    fun save(idempotencyKey: String) = viewModelScope.launch {
        val settings = uiState.value.content().settings
        when (val result = repository.updateSettings(settings, idempotencyKey)) {
            is OperationResult.Success -> updateState { it.content().copy(settings = result.value, loadState = UiLoadState.Loaded) }
            is OperationResult.Failure -> fail(result.error)
        }
    }

    fun clearLocalData() = viewModelScope.launch {
        when (val result = repository.clearLocalData()) {
            is OperationResult.Success -> Unit
            is OperationResult.Failure -> fail(result.error)
        }
    }

    fun requestAccountAnonymization(idempotencyKey: String) = viewModelScope.launch {
        when (val result = repository.requestAccountAnonymization(idempotencyKey)) {
            is OperationResult.Success -> updateState { it.content().copy(anonymization = result.value) }
            is OperationResult.Failure -> fail(result.error)
        }
    }

    private fun updateSettings(transform: (PrivacySettings) -> PrivacySettings) =
        updateState { it.content().copy(settings = transform(it.content().settings)) }

    private fun fail(error: AppError) = updateState { current ->
        val state = current.content()
        PrivacyUiState.Error(error, state.settings, state.anonymization, UiLoadState.Error(error))
    }

    private fun PrivacyUiState.content() = when (this) {
        is PrivacyUiState.Content -> this
        is PrivacyUiState.Error -> PrivacyUiState.Content(settings, anonymization, loadState)
    }
}
