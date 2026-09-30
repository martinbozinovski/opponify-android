package com.opponify.auth.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.opponify.auth.domain.AuthRepository
import com.opponify.auth.model.AuthState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repository: AuthRepository,
) : ViewModel() {
    val authState: StateFlow<AuthState> = repository.authState.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = AuthState.Loading,
    )

    suspend fun signIn(email: String, password: String) = repository.signInWithEmail(email, password)
    suspend fun createAccount(email: String, password: String) = repository.createAccountWithEmail(email, password)
    suspend fun resetPassword(email: String) = repository.sendPasswordReset(email)
    suspend fun sendEmailVerification() = repository.sendEmailVerification()
    suspend fun refreshCurrentUser() = repository.refreshCurrentUser()

    fun startPhoneVerification(
        activity: android.app.Activity,
        phoneNumber: String,
        callbacks: com.opponify.auth.domain.PhoneVerificationCallbacks,
    ) = repository.startPhoneVerification(activity, phoneNumber, callbacks)

    suspend fun linkPhoneVerification(verificationId: String, smsCode: String) =
        repository.linkPhoneVerification(verificationId, smsCode)
    suspend fun signOut() = repository.signOut()
}
