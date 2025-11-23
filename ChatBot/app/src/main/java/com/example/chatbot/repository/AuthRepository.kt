package com.example.chatbot.repository

import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.OtpType
import io.github.jan.supabase.auth.providers.Google
import io.github.jan.supabase.auth.providers.builtin.IDToken
import io.github.jan.supabase.auth.providers.builtin.OTP
import jakarta.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import java.util.UUID

interface AuthRepository {
    suspend fun signInWithOtp(email: String)
    suspend fun signInWithIdToken(idToken: String, nonce: String)
    suspend fun verifyOtp(email: String, token: String)
    suspend fun getCurrentUserId(): String
}

class AuthRepositoryImpl @Inject constructor(
    private val auth: Auth
) : AuthRepository {

    override suspend fun signInWithOtp(email: String) {
        withContext(Dispatchers.IO) {
            auth.signInWith(OTP) {
                this.email = email
            }
        }
    }

    override suspend fun signInWithIdToken(idToken: String, nonce: String) {
        withContext(Dispatchers.IO) {
            auth.signInWith(IDToken) {
                this.provider = Google
                this.idToken = idToken
                this.nonce = nonce
            }
        }
    }

    override suspend fun verifyOtp(email: String, token: String) {
        withContext(Dispatchers.IO) {
            auth.verifyEmailOtp(
                type = OtpType.Email.EMAIL,
                email = email,
                token = token
            )
        }
    }

    override suspend fun getCurrentUserId(): String {
        val userId: String
        withContext(Dispatchers.IO) {
            userId = auth.currentUserOrNull()?.id ?: ""
        }
        return userId
    }
}