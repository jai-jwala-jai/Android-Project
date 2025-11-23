package com.example.chatbot.ui.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.LocalDateTime
import java.util.UUID

@Serializable
data class MessageDto(
    val mid: String = UUID.randomUUID().toString(),
    val sender: String = "",
    val content: String = "",
    @SerialName("chat_id") val chatId: String = "",
    @SerialName("created_at") val createdAt: String = LocalDateTime.now().toString()
)
