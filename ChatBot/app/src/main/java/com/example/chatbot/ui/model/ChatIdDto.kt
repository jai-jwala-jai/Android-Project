package com.example.chatbot.ui.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ChatIdDto(
    val cid: String,
    @SerialName("created_at") val createdAt: String,
)
