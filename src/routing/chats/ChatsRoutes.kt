package com.deledzis.routing.chats

import com.deledzis.API_VERSION
import io.ktor.locations.*

object ChatsRoutes {
    private const val CHATS = "$API_VERSION/chats"
    const val CHATS_GET = CHATS
    const val CHATS_CREATE = CHATS
    const val CHAT_GET = "$CHATS/{id}"
    const val CHAT_SEND_MESSAGE = "$CHATS/{id}"
    const val CHAT_SEARCH = "$CHATS/{id}"

    @Location(CHATS_GET)
    class GetChatsRoute

    @Location(CHATS_CREATE)
    class CreateChatRoute

    @Location(CHAT_GET)
    data class GetChatRoute(val id: Int, val search: String?)

    @Location(CHAT_SEND_MESSAGE)
    data class CreateMessageRoute(val id: Int)

    @Location(CHAT_SEARCH)
    data class ChatSearchRoute(val id: Int, val search: String?)
}