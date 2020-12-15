package com.deledzis.data.model

import java.time.LocalDateTime

data class Message(
    val id: Int,
    val type: Boolean,
    val content: String?,
    val fileName: String? = null,
    val date: LocalDateTime,
    val chatId: Int,
    val authorId: Int
)