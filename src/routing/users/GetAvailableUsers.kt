package com.deledzis.routing.users

import com.deledzis.data.model.User
import com.deledzis.data.model.getInterlocutorId
import com.deledzis.data.repository.Repository
import com.deledzis.data.response.ErrorResponse
import com.deledzis.data.response.UsersResponse
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.http.*
import io.ktor.locations.*
import io.ktor.response.*
import io.ktor.routing.*

fun Route.getAvailableUsers(db: Repository) {
    authenticate("jwt") {
        get<UsersRoute.UsersAvailableRoute> { query ->
            try {
                val user = call.authentication.principal<User>() ?: return@get call.respond(
                    HttpStatusCode.BadRequest,
                    ErrorResponse(406, "Пользователь не найден")
                )
                val interlocutorsIdsFromDb = db.getUserChats(user.id).map {
                    it.getInterlocutorId(user.id)
                }
                val usersFromDb = db.getUsers((query.search ?: "").trim().toLowerCase().trim().toLowerCase())
                    .filterNot { interlocutorsIdsFromDb.contains(it.id) || it.id == user.id }
                val response = UsersResponse(users = usersFromDb)
                call.respond(response)
            } catch (e: Throwable) {
                application.log.error("Failed to find available users", e)
                call.respond(
                    HttpStatusCode.BadRequest,
                    ErrorResponse(400, "Не удалось выполнить запрос (ошибка ${e.localizedMessage})")
                )
            }
        }
    }
}