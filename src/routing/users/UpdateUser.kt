package com.deledzis.routing.users

import com.deledzis.auth.JwtService
import com.deledzis.auth.ServerSession
import com.deledzis.data.model.User
import com.deledzis.data.repository.Repository
import com.deledzis.data.request.UpdateUserRequest
import com.deledzis.data.response.AuthorizedUserResponse
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
            val user = call.authentication.principal<User>()
                ?: return@put call.respond(HttpStatusCode(406, "Authentication Error"))

            val updateUserParameters = call.receive<UpdateUserRequest>()
            val username = updateUserParameters.username
                ?: return@put call.respond(HttpStatusCode(401, "Missing username"))
            val nickname = updateUserParameters.nickname
            val password = updateUserParameters.password
            val newPassword = updateUserParameters.newPassword

            if (password.isNullOrBlank() && !newPassword.isNullOrBlank()) {
                application.log.error("Failed to update user - no password")
                return@put call.respond(HttpStatusCode(413, "Missing password"))
            }

            val oldHash = password?.let { hashFunction(it) }
            val newHash = newPassword?.let { hashFunction(it) }
            try {
                val userFromDb = db.getUser(user.id)
                userFromDb?.id?.let {
                    if (userFromDb.passwordHash != oldHash) {
                        application.log.error("Failed to update user - wrong password")
                        return@put call.respond(HttpStatusCode(414, "Wrong password"))
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
                    } ?: call.respond(HttpStatusCode(415, "Failed to update user"))
                } ?: call.respond(HttpStatusCode(406, "Authentication Error"))
            } catch (e: Throwable) {
                application.log.error("Failed to update user", e)
                call.respond(HttpStatusCode(400, "Failed to execute request (exception ${e.localizedMessage})"))
            }
        }
    }
}