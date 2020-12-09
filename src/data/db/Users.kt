package com.deledzis.data.db

import org.jetbrains.exposed.sql.Table

object Users : Table() {
    val id = integer("id").autoIncrement().primaryKey()
    val username = varchar("username", 100).uniqueIndex()
    val nickname = varchar("name", 100).nullable()
    val passwordHash = varchar("password", 256)
}