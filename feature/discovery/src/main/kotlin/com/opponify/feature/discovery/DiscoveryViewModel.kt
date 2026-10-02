package com.opponify.feature.discovery

import androidx.lifecycle.viewModelScope
import com.opponify.common.architecture.AppError
import com.opponify.common.architecture.BaseViewModel
import com.opponify.common.architecture.OperationResult
import com.opponify.common.architecture.UiLoadState
import com.opponify.model.DiscoveryQuery
import com.opponify.model.Opportunity
import kotlinx.coroutines.launch

sealed interface DiscoveryUiState {
    data object Initial : DiscoveryUiState
    data class Content(val opportunities: List<Opportunity>, val query: DiscoveryQuery, val loadState: UiLoadState = UiLoadState.Loaded) : DiscoveryUiState
    data class Error(val error: AppError) : DiscoveryUiState
}

class DiscoveryViewModel(private val repository: DiscoveryRepository) : BaseViewModel<DiscoveryUiState>(DiscoveryUiState.Initial) {
    fun load(query: DiscoveryQuery = DiscoveryQuery()) {
        updateState { current ->
            if (current is DiscoveryUiState.Content) current.copy(query = query, loadState = UiLoadState.Refreshing)
            else DiscoveryUiState.Content(emptyList(), query, UiLoadState.Loading)
        }
        viewModelScope.launch {
            when (val result = repository.discover(query)) {
                is OperationResult.Success -> updateState { DiscoveryUiState.Content(result.value, query) }
                is OperationResult.Failure -> updateState { DiscoveryUiState.Error(result.error) }
            }
        }
    }

    fun updateQuery(transform: (DiscoveryQuery) -> DiscoveryQuery) = updateState { current ->
        when (current) {
            is DiscoveryUiState.Content -> current.copy(query = transform(current.query))
            else -> current
        }
    }
}
