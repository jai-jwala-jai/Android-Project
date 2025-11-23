package com.example.chatbot.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.chatbot.R
import dev.jeziellago.compose.markdowntext.MarkdownText

@Composable
fun PromptResponseCard(content: String) {
    val context = LocalContext.current

    Row(
        modifier = Modifier.fillMaxWidth(),
    ) {
        MarkdownText(
            markdown = content,
            style = MaterialTheme.typography.bodyLarge,
            syntaxHighlightColor = MaterialTheme.colorScheme.surfaceVariant,
            syntaxHighlightTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
            linkColor = MaterialTheme.colorScheme.primary
        )
    }

    Spacer(modifier = Modifier.heightIn(4.dp))

    IconButton(
        onClick = {
            val clipboardManager =
                context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

            val clipData = ClipData.newPlainText("Copied Text", content)

            clipboardManager.setPrimaryClip(clipData)

            Toast.makeText(context, "Text copied to clipboard", Toast.LENGTH_SHORT).show()
        }
    ) {
        Icon(
            painter = painterResource(R.drawable.copy),
            contentDescription = "Copy",
            modifier = Modifier.size(16.dp)
        )
    }

    HorizontalDivider(
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.8f)
    )
}