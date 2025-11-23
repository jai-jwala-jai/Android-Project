package com.example.chatbot.ui.components

import android.annotation.SuppressLint
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp

@SuppressLint("ConfigurationScreenWidthHeight")
@Composable
fun SkeletonLoading(modifier: Modifier = Modifier) {
    var widthPx by remember { mutableFloatStateOf(0f) }

    val screenWidth = LocalConfiguration.current
    val widthDp80 = screenWidth.screenWidthDp.toFloat() * 0.8f
    val widthDp50 = screenWidth.screenWidthDp.toFloat() * 0.5f

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(4.dp)
    ) {
        repeat(3) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(40.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .onGloballyPositioned { coordinates ->
                            widthPx = coordinates.size.width.toFloat()
                        }
                        .shimmerEffect(widthPx)
                )
                Box(
                    modifier = Modifier
                        .width(widthDp80.dp)
                        .height(40.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .onGloballyPositioned { coordinates ->
                            widthPx = coordinates.size.width.toFloat()
                        }
                        .shimmerEffect(widthPx)
                )
                Box(
                    modifier = Modifier
                        .width(widthDp50.dp)
                        .height(40.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .onGloballyPositioned { coordinates ->
                            widthPx = coordinates.size.width.toFloat()
                        }
                        .shimmerEffect(widthPx)
                )
            }

            Spacer(Modifier.height(20.dp))
        }
    }
}

fun Modifier.shimmerEffect(width: Float): Modifier = composed {
    val infiniteTransition = rememberInfiniteTransition()
    val shimmerX by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = width * 2,
        animationSpec = infiniteRepeatable(
            animation = tween(1300, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    val brush = Brush.linearGradient(
        colors = listOf(
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f),
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
        ),
        start = Offset(shimmerX - (width / 2f), 0f),
        end = Offset(shimmerX, 200f)
    )

    background(brush)
}