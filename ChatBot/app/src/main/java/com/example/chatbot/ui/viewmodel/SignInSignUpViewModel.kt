package com.example.chatbot.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatbot.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.jan.supabase.exceptions.RestException
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SignInSignUpUi(
    val loading: Boolean = false,
    val email: String = "",
    val error: Boolean = false,
    val enable: Boolean = false
)

@HiltViewModel
class SignInSignUpViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _signInSignUpUiState = MutableStateFlow(SignInSignUpUi())
    val signInSignUpUiState = _signInSignUpUiState.asStateFlow()

    fun updateEmail(input: String) {
        _signInSignUpUiState.update {
            it.copy(
                email = input,
                enable = input.isNotBlank(),
                error = false
            )
        }
    }

    fun sendOtp(isSuccess: (Boolean) -> Unit) {
        viewModelScope.launch {
            try {
                _signInSignUpUiState.update { it.copy(loading = true, enable = false) }
                val email = signInSignUpUiState.value.email.trim()

                authRepository.signInWithOtp(email)
                isSuccess(true)

                _signInSignUpUiState.update { it.copy(loading = false) }

            } catch (_: RestException) {
                isSuccess(false)
                _signInSignUpUiState.update {
                    it.copy(error = true, loading = false, enable = true)
                }
            } catch (e: Exception) {
                isSuccess(false)
                println("Error in sending otp: ${e.printStackTrace()}")
            }
        }
    }
}