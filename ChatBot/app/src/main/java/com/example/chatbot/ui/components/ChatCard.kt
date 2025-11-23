package com.example.chatbot.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.example.chatbot.ui.model.Message
import com.example.chatbot.ui.model.formattedDate
import dev.jeziellago.compose.markdowntext.MarkdownText


@Composable
fun ChatCard(
    chat: Message,
    navigateToEditChatScreen: (String) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
            .clickable {
                navigateToEditChatScreen(chat.chatId)
            }
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(),
            verticalArrangement = Arrangement.SpaceBetween,
        ) {

            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = chat.user,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 2
                )
                MarkdownText(
                    markdown = chat.model,
                    syntaxHighlightColor = MaterialTheme.colorScheme.surfaceVariant,
                    syntaxHighlightTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 8,
                    style = TextStyle(color = MaterialTheme.colorScheme.onSurfaceVariant)
                )
            }

            Spacer(Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Text(
                    text = formattedDate(chat.createdAt),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
