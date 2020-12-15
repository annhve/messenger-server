package com.deledzis.routing.chats

import com.deledzis.data.repository.Repository
import com.deledzis.data.request.CreateMessageRequest
import com.deledzis.data.response.ErrorResponse
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.http.*
import io.ktor.locations.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.sessions.*

fun Route.createMessage(db: Repository) {
    authenticate("jwt") {
        post<ChatsRoutes.CreateMessageRoute> {
            val signupParameters = call.receive<CreateMessageRequest>()
            val chatId = signupParameters.chatId
                ?: return@post call.respond(HttpStatusCode(411, "Missing chatId"))
            val authorId = signupParameters.authorId
                ?: return@post call.respond(HttpStatusCode(409, "Missing authorId"))
            val type = signupParameters.type ?: false
            val content = signupParameters.content

            try {
                val newMessage = db.createMessage(chatId, authorId, type, content)
                newMessage?.id?.let {
                    call.respond(HttpStatusCode.OK, ErrorResponse(0, "success"))
                } ?: call.respond(HttpStatusCode(412, "Failed to send message"))
            } catch (e: Throwable) {
                application.log.error("Failed to send message", e)
                call.respond(HttpStatusCode(400, "Failed to execute request (exception ${e.localizedMessage})"))
            }
        }
    }
}