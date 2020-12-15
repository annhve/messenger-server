package com.deledzis.data.response

import com.deledzis.data.model.User
import com.deledzis.util.DateUtils
import java.time.LocalDateTime

data class MessageResponse(
    val id: Int?,
    val type: Boolean?,
    val content: String?,
    val fileName: String? = null,
    val date: String?,
    val chatId: Int?,
    val author: User?
) : Comparable<MessageResponse> {
    override fun compareTo(other: MessageResponse): Int {
        val thisToZdt = LocalDateTime.parse(this.date, DateUtils.DATE_TIME_FULL_FORMAT)
        val otherToZdt = LocalDateTime.parse(other.date, DateUtils.DATE_TIME_FULL_FORMAT)

        return thisToZdt.compareTo(otherToZdt)
    }

}