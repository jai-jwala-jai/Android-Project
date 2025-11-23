package com.example.chatbot.ui.screens.dashboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.chatbot.R
import com.example.chatbot.ui.components.Error
import com.example.chatbot.ui.components.PromptResponseCard
import com.example.chatbot.ui.components.SkeletonLoading
import com.example.chatbot.ui.components.UserCard
import com.example.chatbot.ui.viewmodel.EditArchivedViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditArchivedChatScreen(
    modifier: Modifier = Modifier,
    editArchivedViewModel: EditArchivedViewModel = hiltViewModel(),
    navigateToArchivedChatScreen: () -> Unit
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val listState = rememberLazyListState()
    val messageState by editArchivedViewModel.editArchivedUiState.collectAsStateWithLifecycle()

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            EditArchivedChatTopBar(
                scrollBehavior,
                navigateToArchivedChatScreen,
                editArchivedViewModel::deleteChat
            )
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
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditArchivedChatTopBar(
    scrollBehavior: TopAppBarScrollBehavior,
    navigateToArchivedChatScreen: () -> Unit,
    deleteChat: () -> Unit,
) {
    CenterAlignedTopAppBar(
        title = { Text("Archived Chat") },
        navigationIcon = {
            IconButton(
                onClick = navigateToArchivedChatScreen
            ) {
                Icon(Icons.AutoMirrored.Default.ArrowBack, "Navigate to Archived")
            }
        },
        actions = {
            IconButton(
                onClick = {
                    deleteChat()
                    navigateToArchivedChatScreen()
                }
            ) {
                Icon(
                    painter = painterResource(R.drawable.trash),
                    contentDescription = "Delete",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        },
        scrollBehavior = scrollBehavior,
        colors = TopAppBarDefaults.topAppBarColors(
            MaterialTheme.colorScheme.background
        )
    )
}