package com.opponify.auth.domain

import android.app.Activity
import com.opponify.auth.model.AuthState
import com.opponify.common.architecture.OperationResult
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    val authState: Flow<AuthState>

    suspend fun signInWithEmail(email: String, password: String): OperationResult<Unit>
    suspend fun createAccountWithEmail(email: String, password: String): OperationResult<Unit>
    suspend fun sendPasswordReset(email: String): OperationResult<Unit>
    suspend fun sendEmailVerification(): OperationResult<Unit>
    suspend fun refreshCurrentUser(): OperationResult<Unit>
    suspend fun signOut(): OperationResult<Unit>
    suspend fun getIdToken(forceRefresh: Boolean = false): OperationResult<String>

    fun startPhoneVerification(
        activity: Activity,
        phoneNumber: String,
        callbacks: PhoneVerificationCallbacks,
    )

    suspend fun linkPhoneVerification(verificationId: String, smsCode: String): OperationResult<Unit>
}

interface PhoneVerificationCallbacks {
    fun onCodeSent(verificationId: String, resendToken: Any?)
    fun onVerificationCompleted()
    fun onVerificationFailed(message: String)
}
