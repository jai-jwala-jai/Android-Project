package com.example.chatbot.ui.screens.dashboard

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.chatbot.R
import com.example.chatbot.ui.components.Error
import com.example.chatbot.ui.components.LoadingIndicator
import com.example.chatbot.ui.components.PromptResponseCard
import com.example.chatbot.ui.components.SkeletonLoading
import com.example.chatbot.ui.components.UserCard
import com.example.chatbot.ui.viewmodel.EditChatViewModel
import com.example.chatbot.ui.viewmodel.SendMessageViewModel
import dev.jeziellago.compose.markdowntext.MarkdownText

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditChatScreen(
    modifier: Modifier = Modifier,
    editChatViewModel: EditChatViewModel = hiltViewModel(),
    navigateToChatsScreen: () -> Unit,
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val listState = rememberLazyListState()

    val messageState by editChatViewModel.editChatUiState.collectAsStateWithLifecycle()

    var visible by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(messageState.messages.size) {
        if(messageState.messages.isNotEmpty()) {
            listState.animateScrollToItem(messageState.messages.lastIndex)
        }
    }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        contentWindowInsets = WindowInsets.safeDrawing,
        topBar = {
            EditChatScreenTopBar(
                scrollBehavior,
                navigateToChatsScreen,
                editChatViewModel::deleteChat,
                editChatViewModel::makeChatArchive
            )
        },
        bottomBar = {
            EditChatScreenBottomBar(
                visible = visible,
                completed = messageState.completed,
                sendPrompt = editChatViewModel::sendPrompt,
                cancelResponse = editChatViewModel::cancelResponse
            )
        },
        floatingActionButton = {
            if(!visible) {
                FloatingActionButton(
                    onClick = {
                        visible = true
                    }
                ) {
                    Icon(
                        painter = painterResource(R.drawable.edit),
                        contentDescription = "Edit"
                    )
                }
            }
        }
    ) { innerPadding ->
        when {
            messageState.loading -> SkeletonLoading(modifier = Modifier.padding(innerPadding))
            messageState.error -> Error(modifier = Modifier.padding(innerPadding))
            else -> {
                LazyColumn(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize()
                        .padding(horizontal = 16.dp, vertical = 2.dp),
                    verticalArrangement = Arrangement.spacedBy(15.dp),
                    state = listState
                ) {
                    items(
                        items = messageState.messages,
                        key = { message -> message.mid }
                    ) { message ->
                        if (message.sender == "user") {
                            UserCard(message.content)
                        } else {
                            PromptResponseCard(message.content)
                        }
                    }

                    item {
                        if(messageState.aiThinking) {
                            LoadingIndicator()
                        }
                    }

                    item {
                        if(messageState.aiGeneratingResponse.isNotEmpty()) {
                            Row(
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                MarkdownText(
                                    markdown = messageState.aiGeneratingResponse,
                                    style = MaterialTheme.typography.bodyLarge,
                                    syntaxHighlightColor = MaterialTheme.colorScheme.surfaceVariant,
                                    syntaxHighlightTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                    linkColor = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditChatScreenTopBar(
    scrollBehavior: TopAppBarScrollBehavior,
    navigateToChatsScreen: () -> Unit,
    deleteChat: () -> Unit,
    makeChatArchive: () -> Unit
) {
    val context = LocalContext.current

    CenterAlignedTopAppBar(
        title = { Text("ChatBot") },
        navigationIcon = {
            IconButton(
                onClick = navigateToChatsScreen
            ) {
                Icon(Icons.AutoMirrored.Default.ArrowBack, "Navigate To Chats")
            }
        },
        actions = {
            IconButton(
                onClick = {
                    makeChatArchive()
                    navigateToChatsScreen()

                    Toast.makeText(context, "Chat moved to archive.", Toast.LENGTH_SHORT).show()
                }
            ) {
                Icon(painter = painterResource(R.drawable.archive), "Archive")
            }

            IconButton(
                onClick = {
                    deleteChat()
                    navigateToChatsScreen()
                }
            ) {
                Icon(
                    painter = painterResource(R.drawable.trash),
                    contentDescription = "Delete",
                    tint = MaterialTheme.colorScheme.error
                )
            }

        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.background
        ),
        scrollBehavior = scrollBehavior
    )
}


@Composable
fun EditChatScreenBottomBar(
    visible: Boolean,
    completed: Boolean,
    sendPrompt: (String) -> Unit,
    cancelResponse: () -> Unit,
    sendMessageViewModel: SendMessageViewModel = viewModel(),
) {
    val message by sendMessageViewModel.sendMessageUiState.collectAsStateWithLifecycle()

    val softwareKeyboard = LocalSoftwareKeyboardController.current

    AnimatedVisibility(
        visible = visible,
        enter = expandVertically { -it } + fadeIn()
    ) {
        val color = MaterialTheme.colorScheme.surfaceVariant
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .drawBehind {
                    val strokeWidth = 1.dp.toPx()

                    drawLine(
                        color = color,
                        start = Offset(0f, 0f),
                        end = Offset(size.width, 0f),
                        strokeWidth = strokeWidth
                    )
                }
                .padding(8.dp)
                .imePadding(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextField(
                value = message,
                placeholder = { Text("Continue asking...") },
                onValueChange = sendMessageViewModel::updateMessageUi,
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                ),
                shape = MaterialTheme.shapes.extraLarge,
                trailingIcon = {
                    if(completed) {
                        if(message.isNotBlank()) {
                            IconButton(
                                onClick = {
                                    softwareKeyboard?.hide()

                                    sendPrompt(message)
                                    sendMessageViewModel.updateMessageUi("")
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Default.Send,
                                    contentDescription = "Send"
                                )
                            }
                        }
                    }
                    else {
                        IconButton(
                            onClick = cancelResponse
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.stop),
                                contentDescription = "Send"
                            )
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}