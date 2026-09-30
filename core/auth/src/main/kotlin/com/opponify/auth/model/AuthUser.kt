package com.opponify.auth.model

data class AuthUser(
    val firebaseUid: String,
    val email: String?,
    val phoneNumber: String?,
    val isEmailVerified: Boolean,
    val isPhoneVerified: Boolean,
)
