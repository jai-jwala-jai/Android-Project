package com.example.chatbot.ui.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatbot.repository.AuthRepository
import com.example.chatbot.repository.ChatRepository
import com.example.chatbot.repository.MessageRepository
import com.example.chatbot.ui.model.ChatDto
import com.example.chatbot.ui.model.MessageDto
import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.Content
import com.google.firebase.ai.type.GenerativeBackend
import dagger.hilt.android.lifecycle.HiltViewModel
import io.ktor.util.valuesOf
import jakarta.inject.Inject
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID

data class ChatMessageListUi(
    val loading: Boolean = false,
    val messages: List<MessageDto> = emptyList(),
    val generatingText: String = "",
    val completed: Boolean = true
)

@HiltViewModel
class ChatMessageListViewModel @Inject constructor(
    private val chatRepository: ChatRepository,
    private val messageRepository: MessageRepository,
    private val authRepository: AuthRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val _chatMessageUiState = MutableStateFlow(ChatMessageListUi())
    val chatMessageUiState = _chatMessageUiState.asStateFlow()

    private val _chatCache = MutableStateFlow<List<MessageDto>>(emptyList())

    private val model = Firebase
        .ai(backend = GenerativeBackend.googleAI())
        .generativeModel("gemini-2.5-flash")

    private var job: Job? = null

    init {
        savedStateHandle[KEY] = null
    }

    companion object {
        private const val KEY = "chat_id"
    }

    val chatId: StateFlow<String?> =
        savedStateHandle.getStateFlow(KEY, null)

    fun newChat() {
        savedStateHandle[KEY] = null
        _chatMessageUiState.update { ChatMessageListUi() }
    }

    fun deleteChat() {
        val curChatId = chatId.value

        if (curChatId == null) return

        viewModelScope.launch {
            chatRepository.deleteChat(curChatId)

            newChat()
        }
    }

    fun makeChatArchive() {
        val curChatId = chatId.value

        if (curChatId == null) return

        viewModelScope.launch {
            chatRepository.updateChat(curChatId, true)
        }
    }

    private suspend fun addNewChatIfNeeded(): String? {
        val messages = _chatMessageUiState.value.messages
        val userId = authRepository.getCurrentUserId()

        if (messages.isEmpty() && userId.isNotEmpty()) {
            val chatId = UUID.randomUUID().toString()

            savedStateHandle[KEY] = chatId

            val chat = ChatDto(cid = chatId, userId = userId)
            chatRepository.addChat(chat)

            return chatId
        }

        return chatId.value
    }

    private suspend fun saveMessage(sender: String, content: String) {
        val userChatId = chatId.value ?: addNewChatIfNeeded()
        if (userChatId != null) {
            val messageDto = MessageDto(sender = sender, content = content, chatId = userChatId)
            messageRepository.addMessage(messageDto)
        } else {
            println("Chat Id is null")
        }
    }

    fun cancelResponse() {
        job?.cancel()
        if (_chatMessageUiState.value.generatingText.isEmpty()) {
            _chatMessageUiState.update {
                it.copy(loading = false, completed = true, generatingText = "Response cancelled")
            }
        } else {
            _chatMessageUiState.update { it.copy(loading = false, completed = true) }
        }
    }

    private fun update() {
        val modelContent = _chatMessageUiState.value.generatingText
        val modelMessage = MessageDto(sender = "model", content = modelContent)
        _chatCache.update { state -> state + modelMessage }

        viewModelScope.launch {
            saveMessage(modelMessage.sender, modelMessage.content)
        }

        _chatMessageUiState.update {
            it.copy(
                messages = it.messages + modelMessage,
                generatingText = "",
                completed = true,
                loading = false
            )
        }
    }

    fun sendPromptStream(prompt: String) {
        job?.cancel()

        job = viewModelScope.launch {
            try {
                // User prompt
                val userMessage = MessageDto(sender = "user", content = prompt)
                saveMessage(userMessage.sender, userMessage.content) // Saving to db

                _chatCache.update { state -> state + userMessage } // Setting cache

                _chatMessageUiState.update { it.copy(messages = it.messages + userMessage) } // Expose to ui

                val history = _chatMessageUiState.value.messages.map { msg ->
                    Content.Builder()
                        .setRole(msg.sender)
                        .text(msg.content)
                        .build()
                }

                withContext(Dispatchers.IO) {
                    model.generateContentStream(history)
                        .onStart {
                            _chatMessageUiState.update {
                                it.copy(
                                    loading = true,
                                    completed = false
                                )
                            }
                        }.onCompletion {
                            update()
                        }.catch { e ->
                            _chatMessageUiState.update {
                                it.copy(
                                    loading = false,
                                    completed = true
                                )
                            }
                            println("Stream error: ${e.message}")

                        }.collect { value ->
                            val text = value.text

                            if (!text.isNullOrEmpty()) {
                                if (_chatMessageUiState.value.loading) {
                                    _chatMessageUiState.update { it.copy(loading = false) }
                                }
                                _chatMessageUiState.update {
                                    it.copy(generatingText = it.generatingText + text)
                                }
                            } else {
                                println("Collecting error")
                            }
                        }
                }

            } catch (e: CancellationException) {
                println("Job cancel: ${e.message}")
                update()
            } catch (e: Exception) {
                println("Prompt error: ${e.printStackTrace()}")
                println("Prompt error: ${e.message}")
                _chatMessageUiState.update { it.copy(loading = false, completed = true) }
            }
        }
    }
}