package com.opponify.common.architecture

import org.junit.Assert.assertEquals
import org.junit.Test

private data class TestState(val count: Int)
private class TestViewModel : BaseViewModel<TestState>(TestState(0)) {
    fun increment() = updateState { it.copy(count = it.count + 1) }
}

class BaseViewModelTest {
    @Test
    fun state_updates_are_exposed_read_only() {
        val viewModel = TestViewModel()
        viewModel.increment()
        assertEquals(TestState(1), viewModel.uiState.value)
    }
}
