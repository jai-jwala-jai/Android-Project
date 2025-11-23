package com.example.chatbot.ui.components

import android.widget.Toast
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.chatbot.ui.model.Message
import com.example.chatbot.ui.viewmodel.SwipeViewModel
import kotlinx.coroutines.launch

@Composable
fun Chats(
    chats: List<Message>,
    listState: LazyListState,
    navigateToEditChatScreen: (String) -> Unit,
    modifier: Modifier = Modifier,
    swipeViewModel: SwipeViewModel = hiltViewModel(),
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        state = listState
    ) {
        items(
            items = chats,
            key = { chat -> chat.chatId }
        ) { chat ->
            SwipeToDismissCard(
                onDeleteSwipe = {
                    swipeViewModel.deleteChat(chat.chatId)
                },
                onArchiveSwipe = {
                    scope.launch {
                        val archived = swipeViewModel.isChatArchived(chat.chatId)

                        if (archived) {
                            swipeViewModel.chatArchive(chat.chatId, false)
                            Toast.makeText(context, "Chat removed from archived", Toast.LENGTH_SHORT).show()
                        } else {
                            swipeViewModel.chatArchive(chat.chatId, true)
                            Toast.makeText(context, "Chat moved to archived", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            ) {
                ChatCard(chat, navigateToEditChatScreen)
            }
        }
    }
}
