package com.example.chatbot.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatbot.repository.ChatRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class SwipeViewModel @Inject constructor(
    private val chatRepository: ChatRepository
) : ViewModel() {

    fun deleteChat(cid: String) {
        viewModelScope.launch {
            try {
                chatRepository.deleteChat(cid)
            } catch (e: Exception) {
                println("Swipe to delete error : ${e.message}")
            }
        }
    }

    fun chatArchive(cid: String, archived: Boolean) {
        viewModelScope.launch {
            try {
                chatRepository.updateChat(cid, archived)
            } catch (e: Exception) {
                println("Swipe to archive error : ${e.message}")
            }
        }
    }

    suspend fun isChatArchived(cid: String): Boolean {
        val chatArchived = chatRepository.isArchived(cid)
        return chatArchived.archived
    }
}