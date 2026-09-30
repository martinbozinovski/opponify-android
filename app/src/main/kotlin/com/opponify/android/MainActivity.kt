package com.opponify.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.activity.compose.LocalActivity
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.ViewModelProvider
import com.opponify.auth.domain.PhoneVerificationCallbacks
import com.opponify.auth.model.AuthState
import com.opponify.auth.ui.AuthViewModel
import com.opponify.common.architecture.OperationResult
import com.opponify.designsystem.OpponifyTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            OpponifyTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    val viewModel = remember { ViewModelProvider(this@MainActivity)[AuthViewModel::class.java] }
                    val state by viewModel.authState.collectAsStateWithLifecycle()
                    when (val current = state) {
                        AuthState.Loading -> Text("Loading…", modifier = Modifier.padding(24.dp))
                        AuthState.SignedOut -> AuthScreen(viewModel)
                        is AuthState.SignedIn -> SignedInScreen(viewModel, current)
                        is AuthState.Error -> Text(current.message, modifier = Modifier.padding(24.dp))
                    }
                }
            }
        }
    }
}

@androidx.compose.runtime.Composable
private fun AuthScreen(viewModel: AuthViewModel) {
    var createAccount by remember { mutableStateOf(false) }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var message by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text("Opponify", style = MaterialTheme.typography.headlineLarge)
        Text(if (createAccount) "Create your account" else "Sign in")
        OutlinedTextField(email, { email = it }, label = { Text("Email") }, modifier = Modifier.fillMaxWidth(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email), singleLine = true)
        OutlinedTextField(password, { password = it }, label = { Text("Password") }, modifier = Modifier.fillMaxWidth(), visualTransformation = PasswordVisualTransformation(), singleLine = true)
        Button(
            onClick = {
                scope.launch {
                    val result = if (createAccount) viewModel.createAccount(email, password) else viewModel.signIn(email, password)
                    message = result.toMessage()
                }
            },
            modifier = Modifier.fillMaxWidth(),
        ) { Text(if (createAccount) "Create account" else "Sign in") }
        OutlinedButton(
            onClick = {
                createAccount = !createAccount
                message = null
            },
            modifier = Modifier.fillMaxWidth(),
        ) { Text(if (createAccount) "I already have an account" else "Create an account") }
        if (!createAccount) {
            OutlinedButton(
                onClick = { scope.launch { message = viewModel.resetPassword(email).toMessage() } },
                modifier = Modifier.fillMaxWidth(),
            ) { Text("Reset password") }
        }
        message?.let { Text(it, color = MaterialTheme.colorScheme.error) }
    }
}

@androidx.compose.runtime.Composable
private fun SignedInScreen(viewModel: AuthViewModel, state: AuthState.SignedIn) {
    val activity = LocalActivity.current
    val scope = rememberCoroutineScope()
    var phone by remember { mutableStateOf("") }
    var code by remember { mutableStateOf("") }
    var verificationId by remember { mutableStateOf<String?>(null) }
    var message by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text("Welcome to Opponify", style = MaterialTheme.typography.headlineMedium)
        Text("Account: ${state.user.email ?: "Email unavailable"}")
        Text("Email verified: ${state.user.isEmailVerified}")
        Text("Phone verified: ${state.user.isPhoneVerified}")
        if (!state.user.isEmailVerified) {
            Button(onClick = { scope.launch { message = viewModel.sendEmailVerification().toMessage() } }) {
                Text("Send email verification")
            }
        }
        if (!state.user.isPhoneVerified) {
            OutlinedTextField(phone, { phone = it }, label = { Text("Phone number") }, modifier = Modifier.fillMaxWidth(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone), singleLine = true)
            Button(
                enabled = activity != null,
                onClick = {
                    val host = activity ?: return@Button
                    viewModel.startPhoneVerification(host, phone, object : PhoneVerificationCallbacks {
                        override fun onCodeSent(id: String, resendToken: Any?) {
                            verificationId = id
                            message = "Verification code sent."
                        }
                        override fun onVerificationCompleted() { message = "Phone verified." }
                        override fun onVerificationFailed(messageText: String) { message = messageText }
                    })
                },
            ) { Text("Send SMS code") }
            verificationId?.let { id ->
                OutlinedTextField(code, { code = it }, label = { Text("SMS code") }, modifier = Modifier.fillMaxWidth(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), singleLine = true)
                Button(onClick = { scope.launch { message = viewModel.linkPhoneVerification(id, code).toMessage() } }) { Text("Verify phone") }
            }
        }
        OutlinedButton(onClick = { scope.launch { viewModel.signOut() } }) { Text("Sign out") }
        message?.let { Text(it) }
    }
}

private fun OperationResult<Unit>.toMessage(): String = when (this) {
    is OperationResult.Success -> "Done."
    is OperationResult.Failure -> "The request could not be completed (${error::class.simpleName})."
}
