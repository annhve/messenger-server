package com.deledzis.routing.users

import com.deledzis.auth.JwtService
import com.deledzis.auth.ServerSession
import com.deledzis.data.repository.Repository
import com.deledzis.data.request.LoginRequest
import com.deledzis.data.response.AuthorizedUserResponse
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.auth.jwt.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.locations.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.sessions.*

fun Route.login(
    db: Repository,
    jwtService: JwtService,
    hashFunction: (String) -> String
) {
    post<UsersRoute.UserLoginRoute> {
        val loginParameters = call.receive<LoginRequest>()
        val username = loginParameters.username
            ?: return@post call.respond(HttpStatusCode(401, "Missing username"))
        val password = loginParameters.password
            ?: return@post call.respond(HttpStatusCode(402, "Missing password"))

        val hash = hashFunction(password)
        try {
            val currentUser = db.getUserByUsername(username)
            currentUser?.id?.let {
                if (currentUser.passwordHash == hash) {
                    val authorizedUser = AuthorizedUserResponse(
                        id = it,
                        username = currentUser.username,
                        nickname = currentUser.nickname,
                        accessToken = jwtService.generateToken(currentUser)
                    )
                    call.sessions.set(ServerSession(it))
                    call.respond(authorizedUser)
                } else {
                    call.respond(HttpStatusCode(403, "Wrong password"))
                }
            } ?: call.respond(HttpStatusCode(404, "User not found"))
        } catch (e: Throwable) {
            application.log.error("Failed to sign in user", e)
            call.respond(HttpStatusCode(400, "Failed to execute request (exception ${e.localizedMessage})"))
        }
    }
}