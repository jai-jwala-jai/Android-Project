package com.example.chatbot.ui.screens.dashboard

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.chatbot.R
import com.example.chatbot.ui.components.ChatCard
import com.example.chatbot.ui.components.Chats
import com.example.chatbot.ui.components.Empty
import com.example.chatbot.ui.components.Error
import com.example.chatbot.ui.components.SkeletonLoading
import com.example.chatbot.ui.components.SwipeToDismissCard
import com.example.chatbot.ui.model.Message
import com.example.chatbot.ui.model.formattedDate
import com.example.chatbot.ui.viewmodel.MessageViewModel
import com.example.chatbot.ui.viewmodel.SwipeViewModel
import dev.jeziellago.compose.markdowntext.MarkdownText
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatsScreen(
    modifier: Modifier = Modifier,
    messageViewModel: MessageViewModel = hiltViewModel(),
    navigateToEditChatScreen: (String) -> Unit,
    navigateToNewChat: () -> Unit,
    navigateToHomeScreen: () -> Unit,
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val listState = rememberLazyListState()

    val messageState by messageViewModel.messageUiState.collectAsStateWithLifecycle()

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        contentWindowInsets = WindowInsets.safeDrawing,
        topBar = {
            ChatsScreenTopBar(scrollBehavior, navigateToHomeScreen)
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = navigateToNewChat
            ) {
                Icon(
                    painter = painterResource(R.drawable.new_chat),
                    contentDescription = "New Chat"
                )
            }
        }
    ) { innerPadding ->
        when {
            messageState.loading -> SkeletonLoading(modifier = Modifier.padding(innerPadding))
            messageState.messages.isEmpty() -> {
                Empty(
                    "No chats found",
                    R.drawable.empty_chat,
                    modifier = Modifier.padding(innerPadding)
                )
            }

            messageState.error -> Error(modifier = Modifier.padding(innerPadding))
            else -> {
                Chats(
                    messageState.messages,
                    listState,
                    navigateToEditChatScreen,
                    modifier = Modifier.padding(innerPadding)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatsScreenTopBar(
    scrollBehavior: TopAppBarScrollBehavior,
    navigateToHomeScreen: () -> Unit
) {
    CenterAlignedTopAppBar(
        title = {
            Text("Chats")
        },
        navigationIcon = {
            IconButton(
                onClick = navigateToHomeScreen
            ) {
                Icon(Icons.AutoMirrored.Default.ArrowBack, "Navigate Up")
            }
        },
        scrollBehavior = scrollBehavior,
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.background
        )
    )
}
