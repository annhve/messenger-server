package com.deledzis.data.model

data class Chat(
    val id: Int,
    val authorId: Int,
    val interlocutorId: Int
)

fun Chat.getInterlocutorId(userId: Int): Int = if (authorId == userId) interlocutorId else authorId