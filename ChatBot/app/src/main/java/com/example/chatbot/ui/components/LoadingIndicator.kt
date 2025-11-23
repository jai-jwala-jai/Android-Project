package com.example.chatbot.ui.components

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp

@SuppressLint("ConfigurationScreenWidthHeight")
@Composable
fun LoadingIndicator() {
    val configuration = LocalConfiguration.current
    val width35 = configuration.screenWidthDp * 0.35f
    var widthX by remember { mutableFloatStateOf(0f) }

    Box(
        modifier = Modifier
            .width(width35.dp)
            .height(25.dp)
            .clip(MaterialTheme.shapes.large)
            .onGloballyPositioned { coordinates ->
                widthX = coordinates.size.width.toFloat()
            }
            .shimmerEffect(widthX),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Thinking",
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
