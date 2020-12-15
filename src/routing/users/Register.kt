package com.deledzis.routing.users

import com.deledzis.auth.JwtService
import com.deledzis.auth.ServerSession
import com.deledzis.data.repository.Repository
import com.deledzis.data.request.RegisterRequest
import com.deledzis.data.response.AuthorizedUserResponse
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.auth.jwt.*
import io.ktor.http.*
import io.ktor.locations.*
import io.ktor.locations.post
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.sessions.*

fun Route.register(
    db: Repository,
    jwtService: JwtService,
    hashFunction: (String) -> String
) {
    post<UsersRoute.UserRegisterRoute> {
        val signupParameters = call.receive<RegisterRequest>()
        val username = signupParameters.username
            ?: return@post call.respond(HttpStatusCode(401, "Missing username"))
        val nickname = signupParameters.nickname
        val password = signupParameters.password
            ?: return@post call.respond(HttpStatusCode(402, "Missing password"))

        if (!db.checkUsernameAvailable(username)) {
            return@post call.respond(HttpStatusCode(405, "Already registered"))
        }

        val hash = hashFunction(password)
        try {
            val newUser = db.createUser(username, nickname, hash)
            newUser?.id?.let {
                val authorizedUser = AuthorizedUserResponse(
                    id = it,
                    username = newUser.username,
                    nickname = newUser.nickname,
                    accessToken = jwtService.generateToken(newUser)
                )
                call.sessions.set(ServerSession(it))
                call.respond(authorizedUser)
            } ?: call.respond(HttpStatusCode(405, "Already registered"))
        } catch (e: Throwable) {
            application.log.error("Failed to register user", e)
            call.respond(HttpStatusCode(400, "Failed to execute request (exception ${e.localizedMessage})"))
        }
    }
}