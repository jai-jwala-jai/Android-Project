package com.example.chatbot.repository

import com.example.chatbot.ui.model.ArchivedDto
import com.example.chatbot.ui.model.ChatDto
import com.example.chatbot.ui.model.ChatIdDto
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.postgrest.query.Order
import jakarta.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

interface ChatRepository {
    suspend fun addChat(chat: ChatDto)
    suspend fun deleteChat(cid: String)
    suspend fun fetchChats(): List<ChatDto>
    suspend fun updateChat(cid: String, archived: Boolean)
    suspend fun getChatIds(userId: String, archived: Boolean): List<ChatIdDto>
    suspend fun isArchived(cid: String): ArchivedDto
}

class ChatRepositoryImpl @Inject constructor(
    private val supabase: Postgrest
) : ChatRepository {

    override suspend fun addChat(chat: ChatDto) {
        withContext(Dispatchers.IO) {
            supabase.from("chat").insert(chat)
        }
    }

    override suspend fun deleteChat(cid: String) {
        withContext(Dispatchers.IO) {
            supabase.from("chat").delete {
                filter { ChatDto::cid eq cid }
            }
        }
    }

    override suspend fun fetchChats(): List<ChatDto> {
        val chats: List<ChatDto>
        withContext(Dispatchers.IO) {
            chats = supabase
                .from("chat")
                .select {
                    order(column = "created_at", order = Order.DESCENDING)
                }
                .decodeList<ChatDto>()
        }
        return chats
    }

    override suspend fun updateChat(cid: String, archived: Boolean) {
        withContext(Dispatchers.IO) {
            supabase
                .from("chat")
                .update({ set("archived", archived) }) {
                    filter { ChatDto::cid eq cid }
                }
        }
    }

    override suspend fun getChatIds(userId: String, archived: Boolean): List<ChatIdDto> {
        val chatIds: List<ChatIdDto>
        withContext(Dispatchers.IO) {
            chatIds = supabase
                .from("chat")
                .select(columns = Columns.list("cid", "created_at")) {
                    filter {
                        ChatDto::userId eq userId
                        ChatDto::archived eq archived
                    }
                    order(column = "created_at", order = Order.DESCENDING)
                }
                .decodeList<ChatIdDto>()
        }

        return chatIds
    }

    override suspend fun isArchived(cid: String): ArchivedDto {
        val archived: ArchivedDto

        withContext(Dispatchers.IO) {
            archived = supabase
                .from("chat")
                .select(columns = Columns.list("archived")) {
                    filter { ChatDto::cid eq cid }
                }
                .decodeSingle<ArchivedDto>()
        }
        return archived
    }
}