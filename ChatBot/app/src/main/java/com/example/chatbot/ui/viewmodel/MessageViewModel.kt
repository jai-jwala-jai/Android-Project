package com.example.chatbot.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatbot.repository.ChatRepository
import com.example.chatbot.repository.MessageRepository
import com.example.chatbot.ui.model.ChatDto
import com.example.chatbot.ui.model.Message
import com.example.chatbot.ui.model.MessageDto
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.realtime.PostgresAction
import io.github.jan.supabase.realtime.channel
import io.github.jan.supabase.realtime.decodeOldRecord
import io.github.jan.supabase.realtime.decodeRecord
import io.github.jan.supabase.realtime.postgresChangeFlow
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

data class MessageUi(
    val loading: Boolean = false,
    val messages: List<Message> = emptyList(),
    val error: Boolean = false
)

@HiltViewModel
class MessageViewModel @Inject constructor(
    private val messageRepository: MessageRepository,
    private val chatRepository: ChatRepository,
    private val auth: Auth,
    supabase: SupabaseClient
) : ViewModel() {

    private val _messageUiState = MutableStateFlow(MessageUi())
    val messageUiState = _messageUiState.asStateFlow()

    private val channel = supabase.channel("message_channel_${UUID.randomUUID()}")

    init {
        loadChatsInitialMessage()
        listenToRealTime()
    }

    private fun listenToRealTime() {
        channel.postgresChangeFlow<PostgresAction.Delete>(schema = "public") {
            table = "chat"
        }.map { delete ->
            delete.decodeOldRecord<ChatDto>()
        }.onEach { deletedChat ->
            _messageUiState.update { state ->
                state.copy(
                    messages = state.messages.filterNot { it.chatId == deletedChat.cid }
                )
            }
        }.launchIn(viewModelScope)

        channel.postgresChangeFlow<PostgresAction.Update>(schema = "public") {
            table = "chat"
        }.map { update ->
            update.decodeRecord<ChatDto>()
        }.onEach { updated ->
            _messageUiState.update { state ->
                state.copy(
                    messages = state.messages.filterNot{ it.chatId == updated.cid }
                )
            }
        }.launchIn(viewModelScope)

        viewModelScope.launch { channel.subscribe() }
    }

    private fun loadChatsInitialMessage() {
        viewModelScope.launch {
            _messageUiState.update { it.copy(loading = true, error = false) }
            try {
                val userId = auth.currentUserOrNull()?.id
                if (userId == null) {
                    _messageUiState.update { it.copy(loading = false) }
                    println("User id is null")
                    return@launch
                }

                val result = chatRepository.getChatIds(userId, false)
                result.forEach {
                    val message = messageRepository.getMessage(it.cid)
                    val newMessage = Message(
                        chatId = it.cid,
                        user = message[0].content,
                        model = message[1].content,
                        createdAt = it.createdAt,
                    )
                    _messageUiState.update { state ->
                        state.copy(
                            messages = state.messages + newMessage
                        )
                    }
                }

                _messageUiState.update { it.copy(loading = false) }
            } catch (e: Exception) {
                _messageUiState.update { it.copy(error = true, loading = false) }
                println("Could not fetch the message: ${e.printStackTrace()}")
            }
        }
    }

}