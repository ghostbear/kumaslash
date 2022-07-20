package me.ghostbear.web

import io.ktor.http.CacheControl
import io.ktor.http.ContentType
import io.ktor.http.content.CachingOptions
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.application.install
import io.ktor.server.plugins.cachingheaders.CachingHeaders
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.plugins.cors.routing.CORS
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.routing

fun main(args: Array<String>) = io.ktor.server.cio.EngineMain.main(args)

fun Application.module() {
    install(CORS) {
        anyHost()
    }
    install(CachingHeaders) {
        options { applicationCall, outgoingContent ->
            when (outgoingContent.contentType?.withoutParameters()) {
                ContentType.Application.Json -> CachingOptions(CacheControl.MaxAge(maxAgeSeconds = 300))
                else -> null
            }
        }
    }
    install(ContentNegotiation) {
        json(json)
    }
    routing {
        get("/") {
            call.respondText { "Hello, World!" }
        }
    }
}

