package com.deledzis.data.response

import com.deledzis.data.model.User

data class ChatResponse(
    val id: Int?,
    val interlocutor: User?,
    val messages: List<MessageResponse>?
)