package com.opponify.feature.facility

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.opponify.common.architecture.AppError
import com.opponify.common.architecture.BaseViewModel
import com.opponify.common.architecture.OperationResult
import com.opponify.common.architecture.UiLoadState
import com.opponify.model.Facility
import com.opponify.model.FacilityQuery
import com.opponify.model.FacilitySuggestion
import com.opponify.model.GeoPoint
import kotlinx.coroutines.launch
import java.util.UUID

sealed interface FacilityUiState {
    val facilities: List<Facility>
    val selected: Facility?
    val query: FacilityQuery
    val location: LocationState
    val loadState: UiLoadState
    val suggestion: FacilitySuggestion?

    data class Content(
        override val facilities: List<Facility> = emptyList(),
        override val selected: Facility? = null,
        override val query: FacilityQuery = FacilityQuery(),
        override val location: LocationState = LocationState.Unavailable,
        override val loadState: UiLoadState = UiLoadState.Initial,
        override val suggestion: FacilitySuggestion? = null,
    ) : FacilityUiState

    data class Error(
        val error: AppError,
        override val facilities: List<Facility> = emptyList(),
        override val selected: Facility? = null,
        override val query: FacilityQuery = FacilityQuery(),
        override val location: LocationState = LocationState.Unavailable,
        override val loadState: UiLoadState = UiLoadState.Error(error),
        override val suggestion: FacilitySuggestion? = null,
    ) : FacilityUiState
}

class FacilityViewModel(
    private val repository: FacilityRepository,
    private val locationProvider: LocationProvider,
) : BaseViewModel<FacilityUiState>(FacilityUiState.Content()) {

    init {
        viewModelScope.launch { locationProvider.state.collect { location -> updateState { it.content().copy(location = location) } } }
    }

    fun updateQuery(transform: (FacilityQuery) -> FacilityQuery) = updateState { it.content().copy(query = transform(it.content().query)) }

    fun useCurrentLocation() = viewModelScope.launch { locationProvider.refresh() }

    fun discover() = viewModelScope.launch {
        val query = uiState.value.content().query
        updateState { it.content().copy(loadState = if (it.content().facilities.isEmpty()) UiLoadState.Loading else UiLoadState.Refreshing) }
        when (val result = repository.discover(query)) {
            is OperationResult.Success -> updateState { it.content().copy(facilities = result.value, loadState = UiLoadState.Loaded) }
            is OperationResult.Failure -> fail(result.error)
        }
    }

    fun select(id: UUID) = viewModelScope.launch {
        when (val result = repository.getFacility(id)) {
            is OperationResult.Success -> updateState { it.content().copy(selected = result.value) }
            is OperationResult.Failure -> fail(result.error)
        }
    }

    fun updateSuggestion(suggestion: FacilitySuggestion) = updateState { it.content().copy(suggestion = suggestion) }

    fun submitSuggestion(idempotencyKey: String) = viewModelScope.launch {
        val suggestion = uiState.value.content().suggestion ?: return@launch
        when (val result = repository.suggest(suggestion, idempotencyKey)) {
            is OperationResult.Success -> updateState { it.content().copy(suggestion = result.value) }
            is OperationResult.Failure -> fail(result.error)
        }
    }

    private fun fail(error: AppError) = updateState { current ->
        val state = current.content()
        FacilityUiState.Error(error, state.facilities, state.selected, state.query, state.location, UiLoadState.Error(error), state.suggestion)
    }

    private fun FacilityUiState.content() = when (this) {
        is FacilityUiState.Content -> this
        is FacilityUiState.Error -> FacilityUiState.Content(facilities, selected, query, location, loadState, suggestion)
    }
}
