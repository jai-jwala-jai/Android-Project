package com.example.chatbot.ui.model

data class DrawerItems(
    val label: String,
    val icon: Int,
    val navigateTo: () -> Unit
)
