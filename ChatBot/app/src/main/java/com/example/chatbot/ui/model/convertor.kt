package com.example.chatbot.ui.model

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

fun formattedDate(input: String): String {
    val patter = "d MMM, yyyy · hh:mma"

    val dateTime = LocalDateTime.parse(input)
    val dateTimeFormatter = DateTimeFormatter.ofPattern(patter)
    return dateTime.format(dateTimeFormatter)
}