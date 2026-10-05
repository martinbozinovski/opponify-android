package com.opponify.feature.profile

import com.opponify.common.architecture.OperationResult
import com.opponify.model.AccountAnonymizationRequest
import com.opponify.model.PrivacySettings
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert.assertEquals
import org.junit.Test

private class FakePrivacyRepository : PrivacyRepository {
    var settings = PrivacySettings()
    var clearCalls = 0
    var anonymizationCalls = 0

    override suspend fun loadSettings() = OperationResult.Success(settings)

    override suspend fun updateSettings(settings: PrivacySettings, idempotencyKey: String): OperationResult<PrivacySettings> {
        this.settings = settings
        return OperationResult.Success(settings)
    }

    override suspend fun clearLocalData(): OperationResult<Unit> {
        clearCalls++
        return OperationResult.Success(Unit)
    }

    override suspend fun requestAccountAnonymization(idempotencyKey: String): OperationResult<AccountAnonymizationRequest> {
        anonymizationCalls++
        return OperationResult.Success(AccountAnonymizationRequest(com.opponify.model.AccountAnonymizationStatus.REQUESTED))
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
class PrivacyViewModelTest {
    @Test
    fun privacy_settings_are_explicit_and_saved_through_repository() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        try {
            val repository = FakePrivacyRepository()
            val vm = PrivacyViewModel(repository)
            vm.load()
            advanceUntilIdle()
            vm.setAnalyticsEnabled(true)
            vm.setExtendedDiagnosticsEnabled(true)
            vm.save("privacy-save-1")
            advanceUntilIdle()
            val state = vm.uiState.value as PrivacyUiState.Content
            assertEquals(true, state.settings.analyticsEnabled)
            assertEquals(true, state.settings.extendedDiagnosticsEnabled)
            assertEquals(state.settings, repository.settings)
        } finally {
            Dispatchers.resetMain()
        }
    }

    @Test
    fun destructive_privacy_actions_are_explicit_operations() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        try {
            val repository = FakePrivacyRepository()
            val vm = PrivacyViewModel(repository)
            vm.clearLocalData()
            vm.requestAccountAnonymization("anonymize-1")
            advanceUntilIdle()
            assertEquals(1, repository.clearCalls)
            assertEquals(1, repository.anonymizationCalls)
            val state = vm.uiState.value as PrivacyUiState.Content
            assertEquals(com.opponify.model.AccountAnonymizationStatus.REQUESTED, state.anonymization.status)
        } finally {
            Dispatchers.resetMain()
        }
    }
}
