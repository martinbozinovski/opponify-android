package com.opponify.auth

import com.opponify.auth.model.AuthState
import com.opponify.auth.model.AuthUser
import org.junit.Assert.assertEquals
import org.junit.Test

class AuthStateTest {
    @Test
    fun signed_in_state_preserves_identity_metadata() {
        val user = AuthUser("firebase-uid", "user@example.com", "+38970000000", true, true)
        val state = AuthState.SignedIn(user)
        assertEquals("firebase-uid", (state as AuthState.SignedIn).user.firebaseUid)
        assertEquals(true, state.user.isEmailVerified)
        assertEquals(true, state.user.isPhoneVerified)
    }
}
