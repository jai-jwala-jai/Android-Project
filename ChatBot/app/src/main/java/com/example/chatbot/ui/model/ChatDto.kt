package com.example.chatbot.ui.model

import java.time.LocalDateTime
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ChatDto(
    val cid: String = "",
    @SerialName("user_id") val userId: String = "",
    val archived: Boolean = false,
    @SerialName("created_at") val createdAt: String = LocalDateTime.now().toString()
)
