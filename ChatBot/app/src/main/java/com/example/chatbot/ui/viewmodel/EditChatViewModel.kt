package com.example.chatbot.ui.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatbot.repository.ChatRepository
import com.example.chatbot.repository.MessageRepository
import com.example.chatbot.ui.model.MessageDto
import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.Content
import com.google.firebase.ai.type.GenerativeBackend
import com.google.firebase.ai.type.content
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.jan.supabase.auth.Auth
import jakarta.inject.Inject
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class EditChatUi(
    val loading: Boolean = false,
    val messages: List<MessageDto> = emptyList(),
    val error: Boolean = false,
    val aiThinking: Boolean = false,
    val aiGeneratingResponse: String = "",
    val completed: Boolean = true
)

@HiltViewModel
class EditChatViewModel @Inject constructor(
    private val messageRepository: MessageRepository,
    private val chatRepository: ChatRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _editChatUiState = MutableStateFlow(EditChatUi())
    val editChatUiState = _editChatUiState.asStateFlow()

    private val _chatCache = MutableStateFlow<List<MessageDto>>(emptyList())

    private val chatId: String? = savedStateHandle["curChatId"]

    private var job: Job? = null

    private val model = Firebase
        .ai(backend = GenerativeBackend.googleAI())
        .generativeModel("gemini-2.5-flash")

    init {
        loadMessages()
    }

    private suspend fun savePrompt(sender: String, content: String) {
        if (chatId == null) {
            println("Chat id is null")
            return
        }
        val messageDto = MessageDto(sender = sender, content = content, chatId = chatId)
        messageRepository.addMessage(messageDto)
    }

    fun cancelResponse() {
        job?.cancel()

        if(_editChatUiState.value.aiGeneratingResponse.isEmpty()) {
            _editChatUiState.update { it.copy(aiThinking = false, completed = true, aiGeneratingResponse = "Response cancelled") }
        }
        else {
            _editChatUiState.update { it.copy(aiThinking = false, completed = true) }
        }
    }

    private fun updateContent() {
        val aiResponse = _editChatUiState.value.aiGeneratingResponse
        val modelMessage = MessageDto(sender = "model", content = aiResponse)
        _chatCache.update { state -> state + modelMessage }

        viewModelScope.launch {
            savePrompt(modelMessage.sender, modelMessage.content)
        }
        _editChatUiState.update {
            it.copy(
                aiThinking = false,
                completed = true,
                messages = it.messages + modelMessage,
                aiGeneratingResponse = ""
            )
        }
    }

    fun sendPrompt(prompt: String) {
        job?.cancel()
        try {
            job = viewModelScope.launch {
                val userMessage = MessageDto(sender = "user", content = prompt)
                savePrompt(userMessage.sender, userMessage.content)

                _editChatUiState.update { it.copy(messages = it.messages + userMessage) }

                _chatCache.update { state -> state + userMessage }

                val history = _chatCache.value.map { msg ->
                    Content.Builder()
                        .setRole(msg.sender)
                        .text(msg.content)
                        .build()
                }

                withContext(Dispatchers.IO) {
                    model.generateContentStream(history)
                        .onStart {
                            _editChatUiState.update {
                                it.copy(aiThinking = true, completed = false)
                            }
                        }.onCompletion {
                            updateContent()
                        }.catch { e ->
                            _editChatUiState.update {
                                it.copy(
                                    aiThinking = false,
                                    completed = true,
                                    aiGeneratingResponse = "Stream error!: ${e.message}"
                                )
                            }

                            println("Stream error: ${e.message}")
                        }
                        .collect { value ->
                            val text = value.text

                            if (!text.isNullOrEmpty()) {

                                if(_editChatUiState.value.aiThinking) {
                                    _editChatUiState.update { it.copy(aiThinking = false) }
                                }

                                _editChatUiState.update { state ->
                                    state.copy(aiGeneratingResponse = state.aiGeneratingResponse + text)
                                }
                            } else {
                                println("Text is empty")
                            }
                        }
                }
            }
        } catch (e: CancellationException) {
            updateContent()
            println("Cancel exception: ${e.message}")
        }
        catch (e: Exception) {
            _editChatUiState.update { it.copy(aiThinking = false, completed = true) }
            println("Error in edit chat: ${e.message}")
        }
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

    fun makeChatArchive() {
        if (chatId == null) {
            println("Chat is null")
            return
        }

        viewModelScope.launch {
            try {
                chatRepository.updateChat(chatId, true)
            } catch (e: Exception) {
                println("Delete error: ${e.printStackTrace()}")
            }
        }
    }

    private fun loadMessages() {
        if (chatId == null) {
            _editChatUiState.update { it.copy(loading = false) }
            println("ChatId is null")
            return
        }

        viewModelScope.launch {
            try {
                _editChatUiState.update { it.copy(loading = true, error = false) }

                val messages = messageRepository.listMessages(chatId)
                _editChatUiState.update {
                    it.copy(loading = false, messages = messages)
                }

                messages.forEach { message ->
                    _chatCache.update { state -> state + message }
                }

            } catch (e: Exception) {
                _editChatUiState.update { it.copy(loading = false, error = true) }
                println("Message loading exception:${e.printStackTrace()}")
            }
        }
    }
}