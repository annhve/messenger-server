package com.deledzis.data.db

import org.jetbrains.exposed.sql.Table

object Messages : Table() {
    val id = integer("id").autoIncrement().primaryKey()
    val type = bool("type").default(false)
    val content = varchar("content", 10000).nullable()
    val date = varchar("date", 64)
    val chatId = integer("chat_id").references(Chats.id)
    val authorId = integer("author_id").references(Users.id)
}