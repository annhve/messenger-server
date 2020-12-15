package com.deledzis.data.repository

import com.deledzis.data.model.Chat
import com.deledzis.data.model.Message
import com.deledzis.data.model.User

interface Repository {
    suspend fun createUser(
        username: String,
        nickname: String?,
        passwordHash: String
    ): User?

    suspend fun getUser(userId: Int): User?

    suspend fun getUserByUsername(username: String): User?

    suspend fun getUsers(filter: String?): List<User>

    suspend fun updateUser(userId: Int, username: String, nickname: String?, passwordHash: String?): User?

    suspend fun getUserChats(userId: Int): List<Chat>

    suspend fun getChat(chatId: Int): Chat?

    suspend fun createChat(userId: Int, interlocutorId: Int): Chat?

    suspend fun createMessage(chatId: Int, userId: Int, type: Boolean, content: String?): Message?

    suspend fun getChatMessages(chatId: Int, filter: String?): List<Message>

    suspend fun getLastMessageInChat(chatId: Int): Message?
}