package com.deledzis.data.request

data class CreateMessageRequest(
    val chatId: Int?,
    val authorId: Int?,
    val type: Boolean?,
    val content: String?
)