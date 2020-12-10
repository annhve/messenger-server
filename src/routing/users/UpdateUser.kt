package com.deledzis.routing.users

import com.deledzis.auth.JwtService
import com.deledzis.auth.ServerSession
import com.deledzis.data.model.User
import com.deledzis.data.repository.Repository
import com.deledzis.data.request.UpdateUserRequest
import com.deledzis.data.response.AuthorizedUserResponse
import com.deledzis.data.response.ErrorResponse
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.http.*
import io.ktor.locations.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.sessions.*

fun Route.updateUser(
    db: Repository,
    jwtService: JwtService,
    hashFunction: (String) -> String
) {
    authenticate("jwt") {
        put<UsersRoute.UserUpdateRoute> {
            val user = call.authentication.principal<User>() ?: return@put call.respond(
                HttpStatusCode.BadRequest,
                ErrorResponse(406, "Пользователь не найден")
            )

            val updateUserParameters = call.receive<UpdateUserRequest>()
            val username = updateUserParameters.username
                ?: return@put call.respond(HttpStatusCode.Unauthorized, ErrorResponse(401, "Не указан логин"))
            val nickname = updateUserParameters.nickname
            val password = updateUserParameters.password
            val newPassword = updateUserParameters.newPassword

            if (password.isNullOrBlank() && !newPassword.isNullOrBlank()) {
                application.log.error("Failed to update user - no password")
                return@put call.respond(HttpStatusCode.BadRequest, ErrorResponse(413, "Не указан текущий пароль"))
            }

            val oldHash = password?.let { hashFunction(it) }
            val newHash = newPassword?.let { hashFunction(it) }
            try {
                val userFromDb = db.getUser(user.id)
                userFromDb?.id?.let {
                    if (userFromDb.passwordHash != oldHash) {
                        application.log.error("Failed to update user - wrong password")
                        return@put call.respond(
                            HttpStatusCode.BadRequest,
                            ErrorResponse(414, "Указан неверный текущий пароль")
                        )
                    }

                    val updatedUser = db.updateUser(user.id, username, nickname, newHash)
                    updatedUser?.id?.let {
                        val authorizedUser = AuthorizedUserResponse(
                            id = it,
                            username = updatedUser.username,
                            nickname = updatedUser.nickname,
                            accessToken = jwtService.generateToken(updatedUser)
                        )
                        call.sessions.set(ServerSession(it))
                        call.respond(authorizedUser)
                    } ?: call.respond(
                        HttpStatusCode.BadRequest,
                        ErrorResponse(415, "Не удалось обновить пользователя")
                    )
                } ?: call.respond(HttpStatusCode.BadRequest, ErrorResponse(406, "Пользователь не найден"))
            } catch (e: Throwable) {
                application.log.error("Failed to update user", e)
                call.respond(
                    HttpStatusCode.BadRequest,
                    ErrorResponse(400, "Не удалось выполнить запрос (ошибка ${e.localizedMessage})")
                )
            }
        }
    }
}