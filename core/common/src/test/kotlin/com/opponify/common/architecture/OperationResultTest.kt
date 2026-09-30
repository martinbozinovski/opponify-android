package com.opponify.common.architecture

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class OperationResultTest {
    @Test
    fun success_preserves_value() {
        val result = OperationResult.Success("ok")
        assertTrue(result is OperationResult.Success)
        assertEquals("ok", result.value)
    }

    @Test
    fun failure_preserves_error() {
        val result = OperationResult.Failure(AppError.Conflict)
        assertTrue(result is OperationResult.Failure)
        assertEquals(AppError.Conflict, result.error)
    }
}
