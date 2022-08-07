package me.ghostbear.web.logging

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.routing
import me.ghostbear.common.logging.Logging
import me.ghostbear.web.database

fun Application.module() {
    routing {

        get("/logging") {
            val params = call.receive<Logging>()
            val log = database.loggingQueries.get(params.log, params.guildId) { log, guildId, channelId ->
                Logging(log, guildId, channelId)
            }.executeAsOneOrNull()
            if (log == null) {
                call.respond(HttpStatusCode.NotFound, "Log doesn't exist")
            } else {
                call.respond(HttpStatusCode.OK, log)
            }
        }

        post("/logging") {
            val params = call.receive<Logging>()
            try {
                database.transaction {
                    database.loggingQueries.upsert(params.log, params.guildId, params.channelId)
                }
            } catch (e: Exception) {
                System.err.println(e.message)
            }
            call.respond(HttpStatusCode.OK, "OK")
        }

        delete("/logging") {
            val params = call.receive<Logging>()
            try {
                database.transaction {
                    database.loggingQueries.delete(params.log, params.guildId)
                }
            } catch (e: Exception) {
                System.err.println(e.message)
            }
            call.respond(HttpStatusCode.OK, "OK")
        }

    }
}