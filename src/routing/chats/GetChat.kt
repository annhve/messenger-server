package com.deledzis.routing.chats

import com.deledzis.data.model.User
import com.deledzis.data.model.getInterlocutorId
import com.deledzis.data.repository.Repository
import com.deledzis.data.response.*
import com.deledzis.util.DateUtils
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
                    val messages = db.getChatMessages(chatId = chat.id, filter = (chat.search ?: "").trim().toLowerCase())
                        .sortedByDescending { DateUtils.getDate(it.date).time }
                    val response = ChatResponse(
                        id = it,
                        interlocutor = interlocutor,
                        messages = messages.map {
                            MessageResponse(
                                id = it.id,
                                type = it.type,
                                content = it.content,
                                fileName = it.fileName,
                                date = it.date,
                                chatId = it.chatId,
                                author = if (it.authorId == user.id) user else interlocutor
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