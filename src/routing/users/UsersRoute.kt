package com.deledzis.routing.users

import com.deledzis.API_VERSION
import io.ktor.locations.*

object UsersRoute {
    private const val USERS = "$API_VERSION/users"
    const val USER_LOGIN = "$USERS/login"
    const val USER_REGISTER = "$USERS/register"
    const val USER_DETAILS = "$USERS/{id}"
    const val USER_UPDATE = USERS
    const val USERS_AVAILABLE = USERS

    @Location(USER_LOGIN)
    class UserLoginRoute

    @Location(USER_REGISTER)
    class UserRegisterRoute

    @Location(USER_DETAILS)
    data class UserDetailsRoute(val id: Int)

    @Location(USER_UPDATE)
    class UserUpdateRoute

    @Location(USERS_AVAILABLE)
    data class UsersAvailableRoute(val search: String?)
}