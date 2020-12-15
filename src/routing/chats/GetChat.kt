package com.deledzis.routing.chats

import com.deledzis.data.model.User
import com.deledzis.data.model.getInterlocutorId
import com.deledzis.data.repository.Repository
import com.deledzis.data.response.*
import com.deledzis.util.formatDate
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.http.*
import io.ktor.locations.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.sessions.*

fun Route.getChat(db: Repository) {
    authenticate("jwt") {
        get<ChatsRoutes.GetChatRoute> { chat ->
            try {
                val user = call.authentication.principal<User>() ?: return@get call.respond(
                    HttpStatusCode.BadRequest,
                    ErrorResponse(406, "Пользователь не найден")
                )
                val chatFromDb = db.getChat(chat.id)
                chatFromDb?.id?.let {
                    val interlocutor = db.getUser(chatFromDb.getInterlocutorId(user.id)) ?: return@get call.respond(
                        HttpStatusCode.BadRequest,
                        ErrorResponse(407, "Собеседник не найден")
                    )
                    val messages = db.getChatMessages(
                        chatId = chat.id,
                        filter = (chat.search ?: "").trim().toLowerCase()
                    )
                    val response = ChatResponse(
                        id = it,
                        interlocutor = interlocutor,
                        messages = messages.map { message ->
                            MessageResponse(
                                id = message.id,
                                type = message.type,
                                content = message.content,
                                fileName = message.fileName,
                                date = message.date.formatDate(),
                                chatId = message.chatId,
                                author = if (message.authorId == user.id) user else interlocutor
                            )
                        }
                    )
                    call.respond(response)
                } ?: call.respond(HttpStatusCode.NoContent, ErrorResponse(408, "Чат не найден"))
            } catch (e: Throwable) {
                application.log.error("Failed to get chat ${chat.id}", e)
                call.respond(
                    HttpStatusCode.BadRequest,
                    ErrorResponse(400, "Не удалось выполнить запрос (ошибка ${e.localizedMessage})")
                )
            }
        }
    }
}