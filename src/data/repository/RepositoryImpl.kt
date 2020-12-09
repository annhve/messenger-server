package com.deledzis.data.repository

import com.deledzis.data.db.Chats
import com.deledzis.data.db.DatabaseFactory.dbQuery
import com.deledzis.data.db.Messages
import com.deledzis.data.db.Users
import com.deledzis.data.model.Chat
import com.deledzis.data.model.Message
import com.deledzis.data.model.User
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.statements.InsertStatement

class RepositoryImpl : Repository {
    override suspend fun createUser(username: String, nickname: String?, passwordHash: String): User? {
        var statement: InsertStatement<Number>? = null
        dbQuery {
            statement = Users.insert { user ->
                user[Users.username] = username
                user[Users.nickname] = nickname
                user[Users.passwordHash] = passwordHash
            }
        }
        return rowToUser(statement?.resultedValues?.get(0))
    }

    override suspend fun getUser(userId: Int): User? = dbQuery {
        Users.select { Users.id.eq(userId) }.map { rowToUser(it) }.singleOrNull()
    }

    override suspend fun getUserByUsername(username: String): User? = dbQuery {
        Users.select { Users.username.eq(username) }.map { rowToUser(it) }.singleOrNull()
    }

    override suspend fun getUsers(filter: String?): List<User> = dbQuery {
        Users.select {
            Users.username
                .trim()
                .lowerCase()
                .like("%$filter%") or Users.nickname
                .trim()
                .lowerCase()
                .like("%$filter%")
        }.mapNotNull { rowToUser(it) }
    }

    override suspend fun updateUser(userId: Int, username: String, nickname: String?, passwordHash: String?): User? {
        val rowsUpdated = dbQuery {
            Users.update({ Users.id eq userId }) {
                it[Users.username] = username
                it[Users.nickname] = nickname
                passwordHash?.let { hash -> it[Users.passwordHash] = hash }
            }
        }
        return if (rowsUpdated > 0) getUser(userId) else null
    }

    override suspend fun getUserChats(userId: Int): List<Chat> = dbQuery {
        Chats.select { Chats.authorId.eq(userId) or Chats.interlocutorId.eq(userId) }.mapNotNull { rowToChat(it) }
    }

    override suspend fun getChat(chatId: Int): Chat? = dbQuery {
        Chats.select { Chats.id eq chatId }.map { rowToChat(it) }.singleOrNull()
    }

    override suspend fun createChat(userId: Int, interlocutorId: Int): Chat? {
        var statement: InsertStatement<Number>? = null
        dbQuery {
            statement = Chats.insert { chat ->
                chat[Chats.authorId] = userId
                chat[Chats.interlocutorId] = interlocutorId
            }
        }
        return rowToChat(statement?.resultedValues?.get(0))
    }

    override suspend fun createMessage(
        chatId: Int,
        userId: Int,
        type: Boolean,
        content: String?,
        date: String
    ): Message? {
        var statement: InsertStatement<Number>? = null
        dbQuery {
            statement = Messages.insert { message ->
                message[Messages.chatId] = chatId
                message[Messages.authorId] = userId
                message[Messages.type] = type
                message[Messages.content] = content
                message[Messages.date] = date
            }
        }

        return rowToMessage(statement?.resultedValues?.get(0))
    }

    override suspend fun getChatMessages(chatId: Int, filter: String?): List<Message> = dbQuery {
        Messages.select {
            Messages.chatId eq chatId and Messages.content.trim()
                .lowerCase()
                .like("%$filter%")
        }.mapNotNull { rowToMessage(it) }
    }

    override suspend fun getLastMessageInChat(chatId: Int): Message? = dbQuery {
        return@dbQuery rowToMessage(Messages.select { Messages.chatId eq chatId }.lastOrNull())
    }

    private fun rowToUser(row: ResultRow?): User? {
        if (row == null) {
            return null
        }
        return User(
            id = row[Users.id],
            username = row[Users.username],
            nickname = row[Users.nickname],
            passwordHash = row[Users.passwordHash]
        )
    }

    private fun rowToChat(row: ResultRow?): Chat? {
        if (row == null) {
            return null
        }
        return Chat(
            id = row[Chats.id],
            authorId = row[Chats.authorId],
            interlocutorId = row[Chats.interlocutorId]
        )
    }

    private fun rowToMessage(row: ResultRow?): Message? {
        if (row == null) {
            return null
        }
        return Message(
            id = row[Messages.id],
            type = row[Messages.type],
            content = row[Messages.content],
            date = row[Messages.date],
            chatId = row[Messages.chatId],
            authorId = row[Messages.authorId]
        )
    }
}