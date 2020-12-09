package com.deledzis.data.model

import io.ktor.auth.*
import java.io.Serializable

data class User(
    val id: Int,
    val username: String,
    val nickname: String?,
    val passwordHash: String
) : Serializable, Principal