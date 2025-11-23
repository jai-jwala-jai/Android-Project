package com.example.chatbot.ui.screens

import kotlinx.serialization.Serializable

@Serializable
object Auth

@Serializable
object Dashboard

sealed class AuthRoutes {
    @Serializable
    object SignInSigUpScreen : AuthRoutes()

    @Serializable
    data class OtpVerificationScreen(val emailId: String) : AuthRoutes()
}

sealed class DashboardRoutes {
    @Serializable
    object HomeScreen : DashboardRoutes()

    @Serializable
    object ChatsScreen : DashboardRoutes()

    @Serializable
    object ArchiveChatScreen : DashboardRoutes()

    @Serializable
    data class EditChatScreen(val curChatId: String) : DashboardRoutes()

    @Serializable
    data class EditArchivedChatScreen(val curChatId: String) : DashboardRoutes()
}