package com.deledzis.data.request

data class UpdateUserRequest(
    val id: Int?,
    val username: String?,
    val nickname: String?,
    val password: String?,
    val newPassword: String?
)