package com.deledzis

import com.deledzis.auth.JwtService
import com.deledzis.auth.ServerSession
import com.deledzis.auth.hash
import com.deledzis.data.db.DatabaseFactory
import com.deledzis.data.repository.RepositoryImpl
import com.deledzis.routing.chats.*
import com.deledzis.routing.users.*
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.auth.jwt.*
import io.ktor.features.*
import io.ktor.gson.*
import io.ktor.http.*
import io.ktor.locations.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.sessions.*
import org.slf4j.event.Level
import java.text.DateFormat

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

// Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {
    install(Locations)
    install(Sessions) {
        cookie<ServerSession>("MESSENGER_SESSION")
    }

    DatabaseFactory.init()
    val db = RepositoryImpl()
    val jwtService = JwtService()
    val hashFunction = { s: String -> hash(s) }

    install(Authentication) {
        jwt("jwt") {
            verifier(jwtService.verifier)
            realm = "Messenger Server"
            validate {
                val payload = it.payload
                val claim = payload.getClaim("id")
                val claimString = claim.asInt()
                val user = db.getUser(claimString)
                user
            }
        }
    }

    install(CallLogging) {
        level = Level.INFO
        filter { call -> call.request.path().startsWith("/") }
    }

    install(DefaultHeaders) {
        header("X-Engine", "Ktor") // will send this header with each response
    }

    install(ContentNegotiation) {
        gson {
            setDateFormat(DateFormat.FULL)
            setPrettyPrinting()
        }
    }

    routing {
        get("/") {
            return@get call.respond(HttpStatusCode.OK, "TEST")
        }
        // User routes
        login(db, jwtService, hashFunction)
        register(db, jwtService, hashFunction)
        getUser(db)
        updateUser(db, jwtService, hashFunction)

        // Chats routes
        getChats(db)
        getAvailableUsers(db)
        createChat(db)
        getChat(db)
        createMessage(db)
        searchMessages(db)
    }
}

const val API_VERSION = "/v1"