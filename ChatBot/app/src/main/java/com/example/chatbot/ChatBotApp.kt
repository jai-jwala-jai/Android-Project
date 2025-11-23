package com.example.chatbot

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Build
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.chatbot.ui.components.Loading
import com.example.chatbot.ui.screens.Auth
import com.example.chatbot.ui.screens.AuthRoutes
import com.example.chatbot.ui.screens.Dashboard
import com.example.chatbot.ui.screens.DashboardRoutes
import com.example.chatbot.ui.screens.auth.OtpVerificationScreen
import com.example.chatbot.ui.screens.auth.SignInSigUpScreen
import com.example.chatbot.ui.screens.dashboard.ArchiveChatScreen
import com.example.chatbot.ui.screens.dashboard.ChatsScreen
import com.example.chatbot.ui.screens.dashboard.EditArchivedChatScreen
import com.example.chatbot.ui.screens.dashboard.EditChatScreen
import com.example.chatbot.ui.screens.dashboard.HomeScreen
import com.example.chatbot.ui.viewmodel.ChatMessageListViewModel
import com.example.chatbot.ui.viewmodel.UserSessionViewModel

@Composable
fun ChatBotApp(
    userSessionViewModel: UserSessionViewModel = hiltViewModel()
) {
    NavigationBarColor()
    val userSession by userSessionViewModel.userSessionUiState.collectAsStateWithLifecycle()

    val navController = rememberNavController()

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .navigationBarsPadding(),
        contentWindowInsets = WindowInsets(0)
    ) { innerPadding ->

        AnimatedContent(
            targetState = userSession.loading,
            transitionSpec = { fadeIn() togetherWith fadeOut() }
        ) { isLoading ->

            if (isLoading) {
                Loading(modifier = Modifier.padding(innerPadding))
            }
            else {
                NavHost(
                    navController = navController,
                    startDestination = if (userSession.session) Dashboard else Auth
                ) {
                    navigation<Auth>(startDestination = AuthRoutes.SignInSigUpScreen) {
                        composable<AuthRoutes.SignInSigUpScreen> {
                            SignInSigUpScreen(modifier = Modifier.padding(innerPadding)) { email ->
                                navController.navigate(
                                    route = AuthRoutes.OtpVerificationScreen(emailId = email)
                                ) {
                                    popUpTo(0)
                                }
                            }
                        }
                        composable<AuthRoutes.OtpVerificationScreen> {
                            OtpVerificationScreen(modifier = Modifier.padding(innerPadding))
                        }
                    }
                    navigation<Dashboard>(startDestination = DashboardRoutes.HomeScreen) {
                        composable<DashboardRoutes.HomeScreen> {
                            HomeScreen(
                                modifier = Modifier.padding(innerPadding),
                                navigateToNewChat = {
                                    navController.navigate(
                                        route = DashboardRoutes.HomeScreen
                                    ) {
                                        popUpTo(0)
                                        launchSingleTop = true
                                    }
                                },
                                navigateToChats = {
                                    navController.navigate(
                                        route = DashboardRoutes.ChatsScreen
                                    )
                                },
                                navigateToArchiveChats = {
                                    navController.navigate(
                                        route = DashboardRoutes.ArchiveChatScreen
                                    )
                                }
                            )
                        }

                        composable<DashboardRoutes.ChatsScreen> {
                            ChatsScreen(
                                modifier = Modifier.padding(innerPadding),
                                navigateToEditChatScreen = { chatId ->
                                    navController.navigate(
                                        route = DashboardRoutes.EditChatScreen(curChatId = chatId)
                                    )
                                },
                                navigateToNewChat = {
                                    navController.navigate(
                                        route = DashboardRoutes.HomeScreen
                                    ) {
                                        popUpTo(0)
                                    }
                                }
                            ) {
                                navController.navigateUp()
                            }
                        }

                        composable<DashboardRoutes.ArchiveChatScreen> {
                            ArchiveChatScreen(
                                modifier = Modifier.padding(innerPadding),
                                navigateToEditArchivedChat = { chatId ->
                                    navController.navigate(
                                        route =
                                            DashboardRoutes.EditArchivedChatScreen(curChatId = chatId)
                                    )
                                }
                            ) {
                                navController.navigateUp()
                            }
                        }

                        composable<DashboardRoutes.EditChatScreen> {
                            EditChatScreen(modifier = Modifier.padding(innerPadding)) {
                                navController.navigateUp()
                            }
                        }

                        composable<DashboardRoutes.EditArchivedChatScreen> {
                            EditArchivedChatScreen(modifier = Modifier.padding(innerPadding)) {
                                navController.navigateUp()
                            }
                        }
                    }
                }
            }
        }

    }
}

@SuppressLint("ContextCastToActivity")
@Composable
fun NavigationBarColor() {
    val activity = LocalContext.current as Activity
    val window = activity.window

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        window.isNavigationBarContrastEnforced = false
    }
}
