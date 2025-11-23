package com.example.chatbot.ui.screens.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialException
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.chatbot.R
import com.example.chatbot.ui.viewmodel.GoogleLogInViewModel
import com.example.chatbot.ui.viewmodel.SignInSignUpViewModel
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import io.github.jan.supabase.exceptions.RestException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun SignInSigUpScreen(
    modifier: Modifier = Modifier,
    signInSignUpViewModel: SignInSignUpViewModel = hiltViewModel(),
    navigateToVerificationScreen: (String) -> Unit
) {
    val state by signInSignUpViewModel.signInSignUpUiState.collectAsStateWithLifecycle()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets.safeDrawing
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(10.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Image(
                    painter = painterResource(R.drawable.chat_bot),
                    contentDescription = "Logo Icon",
                    modifier = Modifier.size(100.dp)
                )
                Text(
                    text = "ChatBot",
                    style = MaterialTheme.typography.displayMedium
                )
            }

            Spacer(Modifier.height(30.dp))

            Text(
                text = "Continue with email",
                style = MaterialTheme.typography.headlineSmall
            )

            EmailForm(
                state.loading,
                state.error,
                state.email,
                state.enable,
                signInSignUpViewModel::updateEmail,
                signInSignUpViewModel::sendOtp,
                navigateToVerificationScreen
            )

            ContinueWithGoogle()
        }
    }
}

@Composable
fun EmailForm(
    loading: Boolean,
    error: Boolean,
    email: String,
    enable: Boolean,
    updateEmail: (String) -> Unit,
    sendOtp: ((Boolean) -> Unit) -> Unit,
    navigateToVerificationScreen: (String) -> Unit,
) {
    val textFieldColor = TextFieldDefaults.colors(
        focusedIndicatorColor = Color.Transparent,
        unfocusedIndicatorColor = Color.Transparent
    )

    Spacer(Modifier.height(10.dp))

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "Email",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(horizontal = 8.dp)
        )
        TextField(
            value = email,
            onValueChange = updateEmail,
            placeholder = {
                Text("Enter your email")
            },
            singleLine = true,
            colors = textFieldColor,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            shape = MaterialTheme.shapes.large,
            modifier = Modifier.fillMaxWidth()
        )
        if (error) {
            Text(
                text = "Invalid email",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(horizontal = 8.dp)
            )
        }
    }

    Spacer(Modifier.height(10.dp))

    Button(
        onClick = {
            sendOtp { success ->
                if (success) {
                    navigateToVerificationScreen(email)
                }
            }
        },
        enabled = enable,
        modifier = Modifier.fillMaxWidth()
    ) {
        if (loading) {
            CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
            )
        } else {
            Text("Continue")
        }
    }
}

@Composable
fun ContinueWithGoogle(
    googleLogInViewModel: GoogleLogInViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val onClick: () -> Unit = {
        scope.launch {
            try {
                val credentialManager = CredentialManager.create(context)

                val request = GetCredentialRequest.Builder()
                    .addCredentialOption(googleLogInViewModel.provideGoogleIdOption())
                    .build()

                val result = credentialManager.getCredential(
                    context = context,
                    request = request
                )

                val googleIdTokenCredential = GoogleIdTokenCredential
                    .createFrom(result.credential.data)

                val idToken = googleIdTokenCredential.idToken
                googleLogInViewModel.signInWithGoogle(idToken)

            } catch (e: GetCredentialCancellationException) {
                println("Credential cancellation error: ${e.message}")
            } catch (e: GetCredentialException) {
                println("Credential error: ${e.message}")
            } catch (e: GoogleIdTokenParsingException) {
                println("GoogleIdTokenParsing error: ${e.message}")
            } catch (e: RestException) {
                println("RestException: ${e.message}")
            } catch (e: Exception) {
                println("Google sign in error: ${e.message}")
            }
        }
    }

    Spacer(Modifier.height(20.dp))

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        HorizontalDivider(modifier = Modifier.weight(1f))
        Text(" Or ")
        HorizontalDivider(modifier = Modifier.weight(1f))
    }

    Spacer(Modifier.height(20.dp))

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .clickable {
                onClick()
            }
            .padding(10.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Image(
                painter = painterResource(R.drawable.google),
                contentDescription = "Google",
                modifier = Modifier.size(20.dp)
            )

            Text("Continue with google")

            Text("")
        }
    }
}