package com.example.chatbot.ui.screens.dashboard

import android.annotation.SuppressLint
import android.content.res.Configuration
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import com.example.chatbot.R
import com.example.chatbot.ui.components.ChatListMenu
import com.example.chatbot.ui.components.Loading
import com.example.chatbot.ui.components.LoadingIndicator
import com.example.chatbot.ui.components.PromptResponseCard
import com.example.chatbot.ui.components.UserCard
import com.example.chatbot.ui.components.Welcome
import com.example.chatbot.ui.model.DrawerItems
import com.example.chatbot.ui.viewmodel.ChatMessageListViewModel
import com.example.chatbot.ui.viewmodel.SendMessageViewModel
import com.example.chatbot.ui.viewmodel.UserSessionViewModel
import dev.jeziellago.compose.markdowntext.MarkdownText
import kotlinx.coroutines.launch
import kotlin.math.exp

@SuppressLint("ConfigurationScreenWidthHeight")
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    navigateToNewChat: () -> Unit,
    navigateToChats: () -> Unit,
    navigateToArchiveChats: () -> Unit
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val configuration = LocalConfiguration.current

    val drawerWidth = remember {
        when (configuration.orientation) {
            Configuration.ORIENTATION_LANDSCAPE -> {
                configuration.screenWidthDp * 0.45f
            }
            else -> configuration.screenWidthDp * 0.7f
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                modifier = Modifier.width(drawerWidth.dp),
                windowInsets = WindowInsets.safeDrawing
            ) {
                DrawerSheetContent(
                    navigateToNewChat,
                    navigateToChats,
                    navigateToArchiveChats,
                    drawerState
                )
            }
        },
        gesturesEnabled = drawerState.isOpen
    ) {
        HomeScreenContent(modifier, drawerState)
    }
}

@Composable
fun DrawerSheetContent(
    navigateToNewChat: () -> Unit,
    navigateToChats: () -> Unit,
    navigateToArchiveChats: () -> Unit,
    drawerState: DrawerState,
    userSessionViewModel: UserSessionViewModel = hiltViewModel(),
) {
    val sessionState by userSessionViewModel.userSessionUiState.collectAsStateWithLifecycle()
    val email = sessionState.email
    val name = sessionState.name
    val picture = sessionState.picture

    val drawerItems = remember {
        listOf(
            DrawerItems("New Chat", R.drawable.chat, navigateToNewChat),
            DrawerItems("Chats", R.drawable.message, navigateToChats),
            DrawerItems("Archive Chats", R.drawable.archive, navigateToArchiveChats),
        )
    }

    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(45.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.onSurfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            if (picture != null) {
                AsyncImage(
                    model = picture,
                    contentDescription = "User Profile Picture",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.size(45.dp)
                )
            } else {
                Image(
                    painter = painterResource(R.drawable.unknown_user),
                    contentDescription = "User Profile Picture",
                    modifier = Modifier.size(40.dp)
                )
            }
        }
        if (name != null) {
            Text(
                text = name,
                style = MaterialTheme.typography.headlineSmall,
            )
        }
        Text(
            text = email ?: "",
            style = MaterialTheme.typography.labelMedium,
        )

        Spacer(Modifier.height(4.dp))

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .clickable {
                    userSessionViewModel.signOutUser()
                }
                .padding(8.dp),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    painter = painterResource(R.drawable.logout),
                    contentDescription = "Sign Out",
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.error
                )
                Text(
                    text = "Sign Out",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }

    HorizontalDivider()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        drawerItems.forEach { item ->
            NavigationDrawerItem(
                label = { Text(item.label) },
                selected = false,
                onClick = {
                    item.navigateTo()
                    scope.launch {
                        drawerState.close()
                    }
                },
                icon = {
                    Icon(
                        painter = painterResource(item.icon),
                        contentDescription = item.label
                    )
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreenContent(
    modifier: Modifier = Modifier,
    drawerState: DrawerState,
    chatMessageListViewModel: ChatMessageListViewModel = hiltViewModel(),
) {
    val chatState by chatMessageListViewModel.chatMessageUiState.collectAsStateWithLifecycle()
    val messages = chatState.messages

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val listState = rememberLazyListState()

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.lastIndex)
        }
    }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        contentWindowInsets = WindowInsets.safeDrawing,
        topBar = {
            HomeScreenTopBar(
                scrollBehavior,
                messages.isEmpty(),
                drawerState,
                chatMessageListViewModel::newChat,
                chatMessageListViewModel::deleteChat,
                chatMessageListViewModel::makeChatArchive,
            )
        },
        bottomBar = {
            HomeScreenBottomBar(
                chatState.completed,
                chatMessageListViewModel::sendPromptStream,
                chatMessageListViewModel::cancelResponse
            )
        }
    ) { innerPadding ->
        if (messages.isEmpty()) {
            Welcome(modifier = Modifier.padding(innerPadding))
        } else {
            LazyColumn(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 2.dp),
                verticalArrangement = Arrangement.spacedBy(15.dp),
                state = listState
            ) {
                items(
                    items = messages,
                    key = { message -> message.mid }
                ) { message ->
                    when (message.sender) {
                        "user" -> {
                            UserCard(message.content)
                        }

                        else -> {
                            PromptResponseCard(message.content)
                        }
                    }
                }

                if(chatState.loading) {
                    item {
                        LoadingIndicator()
                    }
                }

                if(chatState.generatingText.isNotEmpty()) {
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            MarkdownText(
                                markdown = chatState.generatingText,
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreenTopBar(
    scrollBehavior: TopAppBarScrollBehavior,
    empty: Boolean,
    drawerState: DrawerState,
    newChat: () -> Unit,
    deleteChat: () -> Unit,
    makeChatArchive: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val softwareKeyboardController = LocalSoftwareKeyboardController.current

    CenterAlignedTopAppBar(
        title = {
            Text("Chat Bot")
        },
        navigationIcon = {
            IconButton(
                onClick = {
                    scope.launch {
                        drawerState.open()
                        softwareKeyboardController?.hide()
                    }
                }
            ) {
                Icon(
                    painter = painterResource(R.drawable.menu),
                    contentDescription = "Menu",
                    modifier = Modifier.size(20.dp)
                )
            }
        },
        actions = {
            if (!empty) {
                IconButton(
                    onClick = newChat
                ) {
                    Icon(
                        painter = painterResource(R.drawable.chat),
                        contentDescription = "New Chat"
                    )
                }

                IconButton(
                    onClick = {
                        expanded = !expanded
                    }
                ) {
                    Icon(Icons.Default.MoreVert, "More")
                }

                ChatListMenu(expanded, deleteChat, makeChatArchive) {
                    expanded = false
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.background
        ),
        scrollBehavior = scrollBehavior
    )
}

@Composable
fun HomeScreenBottomBar(
    completed: Boolean,
    sendMessage: (String) -> Unit,
    stopAi: () -> Unit,
    sendMessageViewModel: SendMessageViewModel = viewModel()
) {
    val message by sendMessageViewModel.sendMessageUiState.collectAsStateWithLifecycle()
    val keyboardController = LocalSoftwareKeyboardController.current

    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    val textFieldColors = TextFieldDefaults.colors(
        focusedIndicatorColor = Color.Transparent,
        unfocusedIndicatorColor = Color.Transparent,
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .imePadding(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextField(
            value = message,
            onValueChange = sendMessageViewModel::updateMessageUi,
            placeholder = {
                Text("Ask anything")
            },
            trailingIcon = {
                if (completed) {
                    if(message.isNotBlank()) {
                        IconButton(
                            onClick = {
                                sendMessage(message)
                                sendMessageViewModel.updateMessageUi("")
                                keyboardController?.hide()
                            }
                        ) {
                            Icon(Icons.AutoMirrored.Default.Send, "Send")
                        }
                    }
                }
                else {
                    IconButton(
                        onClick = stopAi
                    ) {
                        Icon(painterResource(R.drawable.stop), "Stop")
                    }
                }
            },
            colors = textFieldColors,
            shape = MaterialTheme.shapes.extraLarge,
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 120.dp)
                .focusRequester(focusRequester)
        )
    }
}