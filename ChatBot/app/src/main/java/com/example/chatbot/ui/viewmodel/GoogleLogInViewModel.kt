package com.example.chatbot.ui.viewmodel

import androidx.credentials.exceptions.GetCredentialException
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatbot.BuildConfig
import com.example.chatbot.repository.AuthRepository
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.jan.supabase.exceptions.RestException
import jakarta.inject.Inject
import kotlinx.coroutines.launch
import java.security.MessageDigest
import java.util.UUID

@HiltViewModel
class GoogleLogInViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {
    private val rawNonce = UUID.randomUUID().toString()
    private val digest = MessageDigest.getInstance("SHA-256").digest(rawNonce.toByteArray())
    private val hashedNonce = digest.fold("") { acc, next -> acc + "%02x".format(next) }

    fun provideGoogleIdOption(): GetGoogleIdOption {
        return GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setServerClientId(BuildConfig.GOOGLE_SERVER_CLIENT_ID)
            .setNonce(hashedNonce)
            .build()
    }

    fun signInWithGoogle(idToken: String) {
        viewModelScope.launch {
            authRepository.signInWithIdToken(idToken, rawNonce)
        }
    }
}