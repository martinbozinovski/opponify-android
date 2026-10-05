package com.opponify.feature.facility

import com.opponify.common.architecture.OperationResult
import com.opponify.model.Facility
import com.opponify.model.FacilityQuery
import com.opponify.model.FacilitySuggestion
import com.opponify.model.GeoPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.util.UUID

@OptIn(ExperimentalCoroutinesApi::class)
class FacilityViewModelTest {
    private val dispatcher = StandardTestDispatcher()
    private lateinit var repository: FakeFacilityRepository
    private lateinit var location: FakeLocationProvider

    @Before fun setUp() { Dispatchers.setMain(dispatcher); repository = FakeFacilityRepository(); location = FakeLocationProvider() }
    @After fun tearDown() { Dispatchers.resetMain() }

    @Test fun discoveryReturnsApprovedFacilitiesOnlyFromRepositoryContract() = runTest {
        val vm = FacilityViewModel(repository, location)
        vm.discover(); advanceUntilIdle()
        assertEquals(1, (vm.uiState.value as FacilityUiState.Content).facilities.size)
        assertEquals(repository.facility.id, (vm.uiState.value as FacilityUiState.Content).facilities.single().id)
    }

    @Test fun currentLocationIsExposedWithoutPublishingIt() = runTest {
        val vm = FacilityViewModel(repository, location)
        location.state.value = LocationState.Available(GeoPoint(41.99, 21.43)); advanceUntilIdle()
        val state = vm.uiState.value as FacilityUiState.Content
        assertTrue(state.location is LocationState.Available)
        assertEquals(null, state.query.center)
    }

    @Test fun suggestionUsesIdempotencyBoundary() = runTest {
        val vm = FacilityViewModel(repository, location)
        vm.updateSuggestion(repository.suggestion); vm.submitSuggestion("idem-10k"); advanceUntilIdle()
        assertEquals("idem-10k", repository.lastIdempotencyKey)
        assertEquals(repository.suggestion.id, (vm.uiState.value as FacilityUiState.Content).suggestion?.id)
    }

    private class FakeLocationProvider : LocationProvider {
        override val state = MutableStateFlow<LocationState>(LocationState.Unavailable)
        override suspend fun refresh() { state.value = LocationState.Available(GeoPoint(41.99, 21.43)) }
    }

    private class FakeFacilityRepository : FacilityRepository {
        val facility = Facility(UUID.randomUUID(), "Test Court", "Skopje", "Test 1", null, com.opponify.model.FacilityStatus.APPROVED)
        val suggestion = FacilitySuggestion(UUID.randomUUID(), "New Court", "Skopje", null, null, emptyList())
        var lastIdempotencyKey: String? = null
        override suspend fun discover(query: FacilityQuery) = OperationResult.Success(listOf(facility))
        override suspend fun getFacility(id: UUID) = OperationResult.Success(facility)
        override suspend fun suggest(suggestion: FacilitySuggestion, idempotencyKey: String): OperationResult<FacilitySuggestion> { lastIdempotencyKey = idempotencyKey; return OperationResult.Success(suggestion) }
    }
}
