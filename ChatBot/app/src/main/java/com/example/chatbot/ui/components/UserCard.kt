package com.example.chatbot.ui.components

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp

@SuppressLint("ConfigurationScreenWidthHeight")
@Composable
fun UserCard(content: String) {
    val localConfiguration = LocalConfiguration.current
    val widthDp = localConfiguration.screenWidthDp
    val widthDp80Percent = widthDp * 0.8f

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End
    ) {
        Card(
            shape = MaterialTheme.shapes.large,
            modifier = Modifier.widthIn(max = widthDp80Percent.dp)
        ) {
            Text(
                text = content,
                modifier = Modifier.padding(8.dp)
            )
        }
    }
}