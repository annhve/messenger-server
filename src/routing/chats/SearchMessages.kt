package com.deledzis.routing.chats

import com.deledzis.data.repository.Repository
import com.deledzis.data.response.ErrorResponse
import com.deledzis.data.response.MessagesResponse
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.http.*
import io.ktor.locations.*
import io.ktor.response.*
import io.ktor.routing.*

fun Route.searchMessages(db: Repository) {
    authenticate("jwt") {
        get<ChatsRoutes.ChatSearchRoute> { query ->
            try {
                val messages = db.getChatMessages(
                    chatId = query.id,
                    filter = (query.search ?: "").trim().toLowerCase()
                )
                val response = MessagesResponse(messages = messages)
                call.respond(response)
            } catch (e: Throwable) {
                application.log.error("Failed to find messages", e)
                call.respond(
                    HttpStatusCode.BadRequest,
                    ErrorResponse(400, "Не удалось выполнить запрос (ошибка ${e.localizedMessage})")
                )
            }
        }
    }
}