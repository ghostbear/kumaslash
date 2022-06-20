package me.ghostbear.kumaslash.commands.source

import dev.kord.core.Kord
import dev.kord.core.behavior.interaction.response.respond
import dev.kord.core.event.interaction.GuildChatInputCommandInteractionCreateEvent
import dev.kord.rest.builder.interaction.string
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.decodeFromString
import me.ghostbear.data.tachiyomi.model.Extension
import me.ghostbear.data.tachiyomi.model.toMessage
import me.ghostbear.kumaslash.client
import me.ghostbear.kumaslash.json
import me.ghostbear.kumaslash.util.createChatInputCommand
import me.ghostbear.kumaslash.util.on

private const val NAME: String = "source"
private const val DESCRIPTION: String = "Find your missing source based on id"

suspend fun Kord.sourceCommand() {
    createChatInputCommand(NAME, DESCRIPTION) {
        string("id", "Id of the source") {
            required = true
        }
    }
    on<GuildChatInputCommandInteractionCreateEvent>(
        condition = {
            interaction.command.rootName == NAME
        }
    ) {
        val response = interaction.deferEphemeralResponse()
        val id = interaction.command.strings["id"]!!
        val extensions = extensions.first()

        val extension = extensions.find { extension ->
            extension.sources.find { source ->
                source.id == id
            } != null
        }

        if (extension == null) {
            response.respond { content = "Not found" }
            return@on
        }

        response.respond(extension.toMessage())
    }
}

val extensions: Flow<List<Extension>> = flow {
    while (true) {
        val raw =
            client.get("https://raw.githubusercontent.com/tachiyomiorg/tachiyomi-extensions/repo/index.min.json") {
                contentType(ContentType.Application.Json)
            }

        emit(json.decodeFromString(raw.bodyAsText()))
        delay(3600000)
    }
}
