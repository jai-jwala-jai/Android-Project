package com.example.chatbot.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatbot.repository.ChatRepository
import com.example.chatbot.repository.MessageRepository
import com.example.chatbot.ui.model.ChatDto
import com.example.chatbot.ui.model.Message
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.realtime.PostgresAction
import io.github.jan.supabase.realtime.channel
import io.github.jan.supabase.realtime.decodeOldRecord
import io.github.jan.supabase.realtime.decodeRecord
import io.github.jan.supabase.realtime.postgresChangeFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

data class ArchivedUi(
    val loading: Boolean = false,
    val archivedMessages: List<Message> = emptyList(),
    val error: Boolean = false
)

@HiltViewModel
class ArchivedViewModel @Inject constructor(
    private val chatRepository: ChatRepository,
    private val messageRepository: MessageRepository,
    private val auth: Auth,
    supabase: SupabaseClient
) : ViewModel() {

    private val _archivedUiState = MutableStateFlow(ArchivedUi())
    val archivedUiState = _archivedUiState.asStateFlow()

    private val channel = supabase.channel("chat_channel_${UUID.randomUUID()}")

    init {
        loadInitialArchivedChatMessages()
        listenToRealTime()
    }

    private fun listenToRealTime() {
        channel.postgresChangeFlow<PostgresAction.Delete>(schema = "public") {
            table = "chat"
        }.map { delete ->
            delete.decodeOldRecord<ChatDto>()
        }.onEach { deleted ->
            _archivedUiState.update { state ->
                state.copy(
                    archivedMessages = state.archivedMessages.filterNot {
                        it.chatId == deleted.cid
                    }
                )
            }
        }.launchIn(viewModelScope)

        channel.postgresChangeFlow<PostgresAction.Update>(schema = "public") {
            table = "chat"
        }.map { update ->
            update.decodeRecord<ChatDto>()
        }.onEach { updated ->
            _archivedUiState.update { state ->
                state.copy(
                    archivedMessages = state.archivedMessages.filterNot {
                        it.chatId == updated.cid
                    }
                )
            }
        }.launchIn(viewModelScope)

        viewModelScope.launch { channel.subscribe() }
    }

    private fun loadInitialArchivedChatMessages() {
        viewModelScope.launch {
            val userId = auth.currentUserOrNull()?.id

            if(userId == null) {
                println("User id is null")
                return@launch
            }

            _archivedUiState.update { it.copy(loading = true, error = false) }
            try {
                val chatIds = chatRepository.getChatIds(userId, true)
                chatIds.forEach { chat ->
                    val rawMessage = messageRepository.getMessage(chat.cid)
                    val message = Message(
                        chatId = chat.cid,
                        user = rawMessage[0].content,
                        model = rawMessage[1].content,
                        createdAt = chat.createdAt,
                    )

                    _archivedUiState.update { state ->
                        state.copy(
                            archivedMessages = state.archivedMessages + message
                        )
                    }
                }
                _archivedUiState.update { it.copy(loading = false) }
            } catch (e: Exception) {
                _archivedUiState.update { it.copy(loading = false, error = true) }
                println("ArchivedChatMessages error: ${e.printStackTrace()}")
            }
        }
    }
}