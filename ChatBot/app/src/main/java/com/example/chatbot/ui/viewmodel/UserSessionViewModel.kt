package com.example.chatbot.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.status.SessionStatus
import jakarta.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class UserSessionUi(
    val loading: Boolean = false,
    val session: Boolean = false,
    val email: String? = null,
    val name: String? = null,
    val picture: String? = null
)

@HiltViewModel
class UserSessionViewModel @Inject constructor(
    private val auth: Auth
) : ViewModel() {

    private val _userSessionUiState = MutableStateFlow(UserSessionUi())
    val userSessionUiState = _userSessionUiState.asStateFlow()

    init {
        checkSession()
    }

    private fun checkSession() {
        try {
            viewModelScope.launch {
                auth.sessionStatus.collect { status ->
                    when(status) {
                        SessionStatus.Initializing -> {
                            _userSessionUiState.update { it.copy(loading = true) }
                        }
                        is SessionStatus.Authenticated -> {
                            val user = auth.currentUserOrNull()

                            val rawName = user?.userMetadata?.get("name")?.toString()
                            val name = rawName?.replace("\"", "")

                            val rawPicture = user?.userMetadata?.get("picture")?.toString()
                            val picture = rawPicture?.replace("\"", "")

                            _userSessionUiState.update {
                                it.copy(
                                    session = true,
                                    loading = false,
                                    email = user?.email,
                                    name = name,
                                    picture = picture
                                )
                            }
                        }
                        is SessionStatus.NotAuthenticated -> {
                            _userSessionUiState.update { it.copy(session = false, loading = false) }
                        }
                        is SessionStatus.RefreshFailure -> {
                            _userSessionUiState.update { it.copy(loading = true) }
                        }
                    }
                }
            }
        }
        catch (e: Exception) {
            println("Session Error: ${e.printStackTrace()}")
        }
    }

    fun signOutUser() {
        viewModelScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    auth.signOut()
                }
            } catch (e: Exception) {
                println("Sign out error: ${e.printStackTrace()}")
            }
        }
    }
}