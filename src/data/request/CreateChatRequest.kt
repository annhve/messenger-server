package com.deledzis.data.request

data class CreateChatRequest(
    val authorId: Int?,
    val interlocutorId: Int?
)