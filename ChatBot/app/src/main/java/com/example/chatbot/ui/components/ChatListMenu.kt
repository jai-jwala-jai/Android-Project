package com.example.chatbot.ui.components

import android.widget.Toast
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.chatbot.R

@Composable
fun ChatListMenu(
    expanded: Boolean,
    deleteChat: () -> Unit,
    makeChatArchive: () -> Unit,
    onDismissRequest: () -> Unit,
) {
    val context = LocalContext.current

    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismissRequest,
        modifier = Modifier.padding(vertical = 2.dp, horizontal = 4.dp),
        shape = MaterialTheme.shapes.medium
    ) {
        DropdownMenuItem(
            text = { Text("Archive") },
            onClick = {
                makeChatArchive()
                onDismissRequest()

                Toast.makeText(context, "Chat moved to archive", Toast.LENGTH_SHORT).show()
            },
            leadingIcon = {
                Icon(painterResource(R.drawable.archive), "Archive")
            }
        )

        DropdownMenuItem(
            text = {
                Text(
                    text = "Delete",
                    color = MaterialTheme.colorScheme.error
                )
            },
            onClick = deleteChat,
            leadingIcon = {
                Icon(
                    painter = painterResource(R.drawable.trash),
                    contentDescription = "Delete",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        )
    }

}