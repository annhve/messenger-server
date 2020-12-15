package com.deledzis.routing.chats

import com.deledzis.data.repository.Repository
import com.deledzis.data.request.CreateChatRequest
import com.deledzis.data.response.ChatReducedResponse
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.http.*
import io.ktor.locations.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.sessions.*

fun Route.createChat(db: Repository) {
    authenticate("jwt") {
        post<ChatsRoutes.CreateChatRoute> {
            val signupParameters = call.receive<CreateChatRequest>()
            val authorId = signupParameters.authorId
                ?: return@post call.respond(HttpStatusCode(409, "Missing authorId"))
            val interlocutorId = signupParameters.interlocutorId
                ?: return@post call.respond(HttpStatusCode(410, "Missing interlocutorId"))

            try {
                val newChat = db.createChat(authorId, interlocutorId)
                newChat?.id?.let {
                    val interlocutor = db.getUser(interlocutorId)
                        ?: return@post call.respond(HttpStatusCode(407, "User for interlocutorId not found"))
                    val chat = ChatReducedResponse(
                        id = it,
                        interlocutor = interlocutor,
                        lastMessage = null
                    )
                    call.respond(chat)
                } ?: call.respond(HttpStatusCode(416, "Chat is already created"))
            } catch (e: Throwable) {
                application.log.error("Failed to create chat", e)
                call.respond(HttpStatusCode(400, "Failed to execute request (exception ${e.localizedMessage})"))
            }
        }
    }
}