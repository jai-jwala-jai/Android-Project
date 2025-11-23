package com.example.chatbot.ui.screens.dashboard

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.chatbot.R
import com.example.chatbot.ui.components.Chats
import com.example.chatbot.ui.components.Empty
import com.example.chatbot.ui.components.Error
import com.example.chatbot.ui.components.SkeletonLoading
import com.example.chatbot.ui.viewmodel.ArchivedViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArchiveChatScreen(
    modifier: Modifier = Modifier,
    archivedViewModel: ArchivedViewModel = hiltViewModel(),
    navigateToEditArchivedChat: (String) -> Unit,
    navigateToHomeScreen: () -> Unit,
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val archivedState by archivedViewModel.archivedUiState.collectAsStateWithLifecycle()
    val listState = rememberLazyListState()

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        contentWindowInsets = WindowInsets.safeDrawing,
        topBar = {
            ArchiveChatScreenTopBar(scrollBehavior, navigateToHomeScreen)
        }
    ) { innerPadding ->
        when {
            archivedState.loading -> SkeletonLoading(modifier = Modifier.padding(innerPadding))
            archivedState.archivedMessages.isEmpty() -> {
                Empty(
                    text = "No Archived chats found",
                    image = R.drawable.empty_archive,
                    modifier = Modifier.padding(innerPadding)
                )
            }
            archivedState.error -> Error(modifier = Modifier.padding(innerPadding))
            else -> {
                Chats(
                    archivedState.archivedMessages,
                    listState,
                    navigateToEditArchivedChat,
                    modifier = Modifier.padding(innerPadding)
                )
            }
        }

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArchiveChatScreenTopBar(
    scrollBehavior: TopAppBarScrollBehavior,
    navigateToHomeScreen: () -> Unit
) {
    CenterAlignedTopAppBar(
        title = { Text("Archive Chats") },
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