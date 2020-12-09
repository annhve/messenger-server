package com.deledzis.data.response

data class AuthorizedUserResponse(
    val id: Int?,
    val username: String?,
    val nickname: String?,
    val accessToken: String?
)