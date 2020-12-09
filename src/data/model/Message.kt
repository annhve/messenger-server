package com.deledzis.data.model

data class Message(
    val id: Int,
    val type: Boolean,
    val content: String?,
    val fileName: String? = null,
    val date: String,
    val chatId: Int,
    val authorId: Int
)