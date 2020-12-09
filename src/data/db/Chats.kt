package com.deledzis.data.db

import org.jetbrains.exposed.sql.Table

object Chats : Table() {
    val id = integer("id").autoIncrement().primaryKey()
    val authorId = integer("author_id").references(Users.id)
    val interlocutorId = integer("interlocutor_id").references(Users.id)
}