package com.deledzis.data.response

import com.deledzis.data.model.User

data class MessageResponse(
    val id: Int?,
    val type: Boolean?,
    val content: String?,
    val fileName: String? = null,
    val date: String?,
    val chatId: Int?,
    val author: User?
)