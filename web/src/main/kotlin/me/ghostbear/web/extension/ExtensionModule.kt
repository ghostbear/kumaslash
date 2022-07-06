package me.ghostbear.web.extension

import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import me.ghostbear.web.extension.remote.Extension
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.routing
import kotlin.time.Duration.Companion.hours
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.serialization.decodeFromString
import me.ghostbear.common.extensions.Match
import me.ghostbear.database.Database
import me.ghostbear.web.database
import me.ghostbear.web.httpClient
import me.ghostbear.web.json

fun Application.module() {
    val repository = ExtensionRepository(database)
    routing {
        get("/extension/{id}") {
            val id = call.parameters["id"]
            if (id == null) {
                call.respond(HttpStatusCode.BadRequest, "Path parameter {id} must be provided")
                return@get
            }

            val extensions = repository
                .getExtensionsBySourceId(id)
                .sortedByDescending {
                    it.id.startsWith(id)
                }
                .map {
                    Match(
                        it,
                        it.id.replace(id, "**$id**")
                    )
                }

            call.respond(HttpStatusCode.OK, extensions)
        }
        post("/extension/refresh") {
            try {
                repository.refresh()
                call.respond(HttpStatusCode.Accepted)
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, e)
            }
        }
    }
    launch(Dispatchers.IO) {
        while (true) {
            repository.refresh()
            delay(1.hours.inWholeMilliseconds)
        }
    }
}

val sourceMapper = { id: String, name: String, apk: String, pkg: String, version: String, language: String ->
    me.ghostbear.common.extensions.Source(
        id = id,
        name = name,
        apk = apk,
        pkg = pkg,
        version = version,
        lang = language
    )
}

class ExtensionRepository(
    private val database: Database
) {

    fun getExtensions(): List<me.ghostbear.common.extensions.Source> {
        return database.extensionQueries.getAll(sourceMapper).executeAsList()
    }

    fun getExtensionsBySourceId(sourceId: String): List<me.ghostbear.common.extensions.Source> {
        return database.extensionQueries.getSourceById(sourceId, sourceMapper).executeAsList()
    }

    suspend fun refresh() {
        val raw = httpClient.get(URL) {
            contentType(ContentType.Application.Json)
        }

        val extensions = json.decodeFromString<List<Extension>>(raw.bodyAsText())

        database.extensionQueries.transaction {
            extensions.forEach { extension ->
                extension.sources.forEach { source ->
                    database.extensionQueries.upsert(
                        source.id,
                        extension.name,
                        extension.apk,
                        extension.pkg,
                        extension.version,
                        extension.language
                    )
                }
            }
        }
    }
}

private const val URL = "https://raw.githubusercontent.com/tachiyomiorg/tachiyomi-extensions/repo/index.min.json"

fun Set<Extension>.filterBySourceId(id: String) =
    filter { extension ->
        extension.sources.any { source ->
            source.id.contains(id)
        }
    }

