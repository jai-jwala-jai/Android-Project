package com.example.chatbot.ui.viewmodel

import androidx.lifecycle.SavedStateHandle
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

data class OtpVerificationUi(
    val loading: Boolean = false,
    val otpValues: List<String> = List(6) {""},
    val error: Boolean = false
)

@HiltViewModel
class OtpVerificationViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _otpUiState = MutableStateFlow(OtpVerificationUi())
    val otpUiState = _otpUiState.asStateFlow()

    private val emailId: String? = savedStateHandle["emailId"]

    fun updateOtpUi(index: Int, text: String) {
        _otpUiState.update { currentState ->
            currentState.copy(
                otpValues = currentState.otpValues.toMutableList().also { it[index] = text },
            )
        }
    }

    fun verifyOtp() {
        viewModelScope.launch {
            try {
                _otpUiState.update { it.copy(loading = true) }

                emailId?.let { email ->
                    val token = otpUiState.value.otpValues.joinToString("")
                    authRepository.verifyOtp(email, token)

                    _otpUiState.update { it.copy(loading = false) }
                }
            }
            catch (_: RestException) {
                _otpUiState.update {
                    it.copy(error = true, loading = false)
                }
            }
            catch (e: Exception) {
                println("Error: ${e.printStackTrace()}")
            }
        }
    }
}