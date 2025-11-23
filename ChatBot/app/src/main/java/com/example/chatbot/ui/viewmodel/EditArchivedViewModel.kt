package com.example.chatbot.ui.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatbot.repository.ChatRepository
import com.example.chatbot.repository.MessageRepository
import com.example.chatbot.ui.model.MessageDto
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class EditArchivedUi(
    val loading: Boolean = false,
    val messages: List<MessageDto> = emptyList(),
    val error: Boolean = false
)

@HiltViewModel
class EditArchivedViewModel @Inject constructor(
    private val messageRepository: MessageRepository,
    private val chatRepository: ChatRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _editArchivedUiState = MutableStateFlow(EditArchivedUi())
    val editArchivedUiState = _editArchivedUiState.asStateFlow()

    private val chatId: String? = savedStateHandle["curChatId"]

    init {
        loadMessages()
    }

    fun deleteChat() {
        if (chatId == null) {
            println("Chat id is null")
            return
        }

        viewModelScope.launch {
            try {
                chatRepository.deleteChat(chatId)
            } catch (e: Exception) {
                println("Delete error: ${e.printStackTrace()}")
            }
        }
    }


    private fun loadMessages() {
        if(chatId == null) {
            println("Chat is null")
            return
        }

        _editArchivedUiState.update { it.copy(loading = true, error = false) }
        viewModelScope.launch {
            try {
                val archivedMessages = messageRepository.listMessages(chatId)
                _editArchivedUiState.update {
                    it.copy(loading = false, messages = archivedMessages)
                }
            } catch (e: Exception) {
                _editArchivedUiState.update { it.copy(loading = false, error = true) }
                println("Archived chat edit error: ${e.printStackTrace()}")
            }
        }
    }
}