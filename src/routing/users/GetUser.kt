package com.deledzis.routing.users

import com.deledzis.data.repository.Repository
import com.deledzis.data.response.ErrorResponse
import com.deledzis.data.response.UserResponse
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.http.*
import io.ktor.locations.*
import io.ktor.response.*
import io.ktor.routing.*

fun Route.getUser(db: Repository) {
    authenticate("jwt") {
        get<UsersRoute.UserDetailsRoute> { userDetails ->
            try {
                val userFromDb = db.getUser(userDetails.id)
                userFromDb?.id?.let {
                    val user = UserResponse(
                        id = it,
                        username = userFromDb.username,
                        nickname = userFromDb.nickname
                    )
                    call.respond(user)
                } ?: call.respond(HttpStatusCode.NoContent, ErrorResponse(406, "Пользователь не найден"))
            } catch (e: Throwable) {
                application.log.error("Failed to find user by id ${userDetails.id}", e)
                call.respond(
                    HttpStatusCode.BadRequest,
                    ErrorResponse(400, "Не удалось выполнить запрос (ошибка ${e.localizedMessage})")
                )
            }
        }
    }
}