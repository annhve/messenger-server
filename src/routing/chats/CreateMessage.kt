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
                ?: return@post call.respond(HttpStatusCode.Unauthorized, ErrorResponse(411, "Не указан чат"))
            val authorId = signupParameters.authorId
                ?: return@post call.respond(HttpStatusCode.Unauthorized, ErrorResponse(409, "Не указан пользователь"))
            val type = signupParameters.type ?: false
            val content = signupParameters.content

            try {
                val newMessage = db.createMessage(chatId, authorId, type, content)
                newMessage?.id?.let {
                    call.respond(HttpStatusCode.OK, ErrorResponse(0, "успешно"))
                } ?: call.respond(
                    HttpStatusCode.BadRequest,
                    ErrorResponse(412, "Не удалось отправить сообщение")
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