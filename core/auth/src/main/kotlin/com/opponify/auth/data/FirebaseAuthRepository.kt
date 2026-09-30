package com.opponify.auth.data

import android.app.Activity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.FirebaseException
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.opponify.auth.domain.AuthRepository
import com.opponify.auth.domain.PhoneVerificationCallbacks
import com.opponify.auth.model.AuthState
import com.opponify.auth.model.AuthUser
import com.opponify.common.architecture.AppError
import com.opponify.common.architecture.OperationResult
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class FirebaseAuthRepository @Inject constructor(
    private val auth: FirebaseAuth,
) : AuthRepository {
    override val authState: Flow<AuthState> = callbackFlow {
        trySend(currentState())
        val listener = FirebaseAuth.AuthStateListener { trySend(currentState()) }
        auth.addAuthStateListener(listener)
        awaitClose { auth.removeAuthStateListener(listener) }
    }

    override suspend fun signInWithEmail(email: String, password: String): OperationResult<Unit> =
        runAuthOperation {
            auth.signInWithEmailAndPassword(email.trim(), password).await()
        }

    override suspend fun createAccountWithEmail(email: String, password: String): OperationResult<Unit> =
        runAuthOperation {
            auth.createUserWithEmailAndPassword(email.trim(), password).await()
        }

    override suspend fun sendPasswordReset(email: String): OperationResult<Unit> =
        runAuthOperation {
            auth.sendPasswordResetEmail(email.trim()).await()
        }

    override suspend fun sendEmailVerification(): OperationResult<Unit> = runAuthOperation {
        val user = auth.currentUser ?: throw IllegalStateException("No authenticated user")
        user.sendEmailVerification().await()
    }

    override suspend fun refreshCurrentUser(): OperationResult<Unit> = runAuthOperation {
        auth.currentUser?.reload()?.await() ?: throw IllegalStateException("No authenticated user")
    }

    override suspend fun signOut(): OperationResult<Unit> = try {
        auth.signOut()
        OperationResult.Success(Unit)
    } catch (_: Exception) {
        OperationResult.Failure(AppError.Unknown)
    }

    override suspend fun getIdToken(forceRefresh: Boolean): OperationResult<String> = try {
        val user = auth.currentUser ?: return OperationResult.Failure(AppError.Unauthorized)
        val token = user.getIdToken(forceRefresh).await().token
            ?: return OperationResult.Failure(AppError.Unauthorized)
        OperationResult.Success(token)
    } catch (e: Exception) {
        OperationResult.Failure(mapAuthError(e))
    }

    override fun startPhoneVerification(
        activity: Activity,
        phoneNumber: String,
        callbacks: PhoneVerificationCallbacks,
    ) {
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber.trim())
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(activity)
            .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                override fun onVerificationCompleted(credential: com.google.firebase.auth.PhoneAuthCredential) {
                    val currentUser = auth.currentUser
                    if (currentUser == null) {
                        callbacks.onVerificationFailed("Authentication session is unavailable.")
                        return
                    }
                    currentUser.linkWithCredential(credential)
                        .addOnSuccessListener { callbacks.onVerificationCompleted() }
                        .addOnFailureListener { callbacks.onVerificationFailed(it.message ?: "Phone verification failed.") }
                }

                override fun onVerificationFailed(exception: FirebaseException) {
                    callbacks.onVerificationFailed(exception.message ?: "Phone verification failed.")
                }

                override fun onCodeSent(
                    verificationId: String,
                    token: PhoneAuthProvider.ForceResendingToken,
                ) {
                    callbacks.onCodeSent(verificationId, token)
                }
            })
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    override suspend fun linkPhoneVerification(
        verificationId: String,
        smsCode: String,
    ): OperationResult<Unit> = try {
        val user = auth.currentUser ?: return OperationResult.Failure(AppError.Unauthorized)
        val credential = PhoneAuthProvider.getCredential(verificationId, smsCode)
        user.linkWithCredential(credential).await()
        OperationResult.Success(Unit)
    } catch (e: Exception) {
        OperationResult.Failure(mapAuthError(e))
    }

    private fun currentState(): AuthState {
        val user = auth.currentUser ?: return AuthState.SignedOut
        return AuthState.SignedIn(
            AuthUser(
                firebaseUid = user.uid,
                email = user.email,
                phoneNumber = user.phoneNumber,
                isEmailVerified = user.isEmailVerified,
                isPhoneVerified = user.phoneNumber != null,
            ),
        )
    }

    private suspend fun runAuthOperation(block: suspend () -> Unit): OperationResult<Unit> = try {
        block()
        OperationResult.Success(Unit)
    } catch (e: Exception) {
        OperationResult.Failure(mapAuthError(e))
    }

    private fun mapAuthError(exception: Exception): AppError = when (exception) {
        is FirebaseAuthException -> when (exception.errorCode) {
            "ERROR_INVALID_EMAIL", "ERROR_WEAK_PASSWORD", "ERROR_MISSING_PASSWORD" -> AppError.Validation
            "ERROR_USER_NOT_FOUND", "ERROR_WRONG_PASSWORD", "ERROR_INVALID_CREDENTIAL" -> AppError.Unauthorized
            "ERROR_EMAIL_ALREADY_IN_USE" -> AppError.Conflict
            "ERROR_TOO_MANY_REQUESTS" -> AppError.RateLimited
            else -> AppError.Unknown
        }
        else -> AppError.Unknown
    }
}
