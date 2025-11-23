package com.example.chatbot.repository

import com.example.chatbot.ui.model.ChatDto
import com.example.chatbot.ui.model.MessageDto
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.query.Order
import jakarta.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

interface MessageRepository {
    suspend fun addMessage(message: MessageDto)
    suspend fun getMessage(cid: String): List<MessageDto>
    suspend fun listMessages(cid: String): List<MessageDto>
}

class MessageRepositoryImpl @Inject constructor(
    private val supabase: Postgrest
) : MessageRepository {

    override suspend fun addMessage(message: MessageDto) {
        withContext(Dispatchers.IO) {
            supabase.from("message").insert(message)
        }
    }

    override suspend fun getMessage(cid: String): List<MessageDto> {
        val messages: List<MessageDto>
        withContext(Dispatchers.IO) {
            messages = supabase
                .from("message")
                .select {
                    limit(2)
                    filter { MessageDto::chatId eq cid }
                    order("created_at", Order.ASCENDING)
                }
                .decodeList<MessageDto>()
        }
        return messages
    }

    override suspend fun listMessages(cid: String): List<MessageDto> {
        val messages: List<MessageDto>

        withContext(Dispatchers.IO) {
            messages = supabase
                .from("message")
                .select {
                    filter { MessageDto::chatId eq cid }
                    order("created_at", Order.ASCENDING)
                }
                .decodeList<MessageDto>()
        }

        return messages
    }
}