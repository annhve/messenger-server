package com.deledzis.data.response

import com.deledzis.data.model.User

data class ChatReducedResponse(
    val id: Int?,
    val interlocutor: User?,
    val lastMessage: MessageResponse?
)