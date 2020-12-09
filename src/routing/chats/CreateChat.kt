package com.deledzis.routing.chats

import com.deledzis.data.repository.Repository
import com.deledzis.data.request.CreateChatRequest
import com.deledzis.data.response.ChatReducedResponse
import com.deledzis.data.response.ErrorResponse
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
                ?: return@post call.respond(HttpStatusCode.Unauthorized, ErrorResponse(409, "Не указан пользователь"))
            val interlocutorId = signupParameters.interlocutorId
                ?: return@post call.respond(HttpStatusCode.Unauthorized, ErrorResponse(410, "Не указан собеседник"))

            try {
                val newChat = db.createChat(authorId, interlocutorId)
                newChat?.id?.let {
                    val interlocutor = db.getUser(interlocutorId) ?: return@post call.respond(
                        HttpStatusCode.BadRequest,
                        ErrorResponse(407, "Собеседник не найден")
                    )
                    val chat = ChatReducedResponse(
                        id = it,
                        interlocutor = interlocutor,
                        lastMessage = null
                    )
                    call.respond(chat)
                } ?: call.respond(
                    HttpStatusCode.BadRequest,
                    ErrorResponse(411, "Диалог уже создан")
                )
            } catch (e: Throwable) {
                application.log.error("Failed to register user", e)
                call.respond(
                    HttpStatusCode.BadRequest,
                    ErrorResponse(400, "Не удалось выполнить запрос (ошибка ${e.localizedMessage})")
                )
            }
        }
    }
}