package com.deledzis.data.db

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.datetime

object Messages : Table() {
    val id = integer("id").autoIncrement().primaryKey()
    val type = bool("type").default(false)
    val content = varchar("content", 10000).nullable()
    val date = datetime("date")
    val chatId = integer("chat_id").references(Chats.id)
    val authorId = integer("author_id").references(Users.id)
}