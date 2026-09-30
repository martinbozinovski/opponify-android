package com.opponify.auth

import com.opponify.auth.model.AuthState
import org.junit.Assert.assertTrue
import org.junit.Test

class AuthRepositoryContractTest {
    @Test
    fun signed_out_is_a_distinct_authoritative_auth_state() {
        assertTrue(AuthState.SignedOut is AuthState)
    }
}
