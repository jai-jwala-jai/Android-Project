package com.example.chatbot.ui.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class SendMessageViewModel : ViewModel() {
    private val _sendMessageUiState = MutableStateFlow("")
    val sendMessageUiState = _sendMessageUiState.asStateFlow()

    fun updateMessageUi(input: String) {
        _sendMessageUiState.update { input }
    }
}