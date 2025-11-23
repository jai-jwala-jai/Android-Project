package com.example.chatbot.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxState
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.chatbot.R

@Composable
fun SwipeToDismissCard(
    onDeleteSwipe: () -> Unit,
    onArchiveSwipe: () -> Unit,
    content: @Composable () -> Unit
) {
    val swipeToDismissBoxState = rememberSwipeToDismissBoxState(
        confirmValueChange = { value ->
            if(value == SwipeToDismissBoxValue.EndToStart) onDeleteSwipe()
            else if(value == SwipeToDismissBoxValue.StartToEnd) onArchiveSwipe()
            value != SwipeToDismissBoxValue.StartToEnd
        },
        positionalThreshold = {totalDistance -> totalDistance * 0.5f}
    )

    SwipeToDismissBox(
        state = swipeToDismissBoxState,
        modifier = Modifier.fillMaxSize(),
        backgroundContent = {
            SwipeToDismissBgContent(swipeToDismissBoxState)
        }
    ) {
        content()
    }

    HorizontalDivider(
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.8f)
    )
}

@Composable
fun SwipeToDismissBgContent(
    state: SwipeToDismissBoxState
) {
    when(state.dismissDirection) {
        SwipeToDismissBoxValue.StartToEnd -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .padding(12.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Icon(
                    painter = painterResource(R.drawable.archive),
                    contentDescription = "Archive",
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
        SwipeToDismissBoxValue.EndToStart -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.errorContainer)
                    .padding(12.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                Icon(
                    painter = painterResource(R.drawable.trash),
                    contentDescription = "Delete",
                    tint = MaterialTheme.colorScheme.onErrorContainer
                )
            }
        }
        SwipeToDismissBoxValue.Settled -> {}
    }
}