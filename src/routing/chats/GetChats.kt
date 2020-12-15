package com.deledzis.routing.chats

import com.deledzis.data.model.User
import com.deledzis.data.model.getInterlocutorId
import com.deledzis.data.repository.Repository
import com.deledzis.data.response.ChatReducedResponse
import com.deledzis.data.response.ChatsResponse
import com.deledzis.data.response.ErrorResponse
import com.deledzis.data.response.MessageResponse
import com.deledzis.util.formatDate
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.http.*
import io.ktor.locations.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.sessions.*

fun Route.getChats(db: Repository) {
    authenticate("jwt") {
        get<ChatsRoutes.GetChatsRoute> {
            try {
                val user = call.authentication.principal<User>() ?: return@get call.respond(
                    HttpStatusCode.BadRequest,
                    ErrorResponse(406, "Пользователь не найден")
                )
                val chatsFromDb = db.getUserChats(user.id).map {
                    val lastMessage = db.getLastMessageInChat(it.id)
                    val interlocutorId = it.getInterlocutorId(user.id)
                    val interlocutor = db.getUser(interlocutorId)
                    ChatReducedResponse(
                        id = it.id,
                        interlocutor = interlocutor,
                        lastMessage = lastMessage?.let { message ->
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
                }

                val response = ChatsResponse(
                    chats = chatsFromDb.filterNot { it.interlocutor == null }
                        .sortedByDescending { it.lastMessage }
                )
                call.respond(response)
            } catch (e: Throwable) {
                application.log.error("Failed to get user chats", e)
                call.respond(
                    HttpStatusCode.BadRequest,
                    ErrorResponse(400, "Не удалось выполнить запрос (ошибка ${e.localizedMessage})")
                )
            }
        }
    }
}