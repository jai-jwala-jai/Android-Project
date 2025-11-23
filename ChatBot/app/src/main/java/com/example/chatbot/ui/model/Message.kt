package com.example.chatbot.ui.model

data class Message(
    val chatId: String,
    val user: String,
    val model: String,
    val createdAt: String
)
