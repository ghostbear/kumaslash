package me.ghostbear.kumaslash.commands.source

import dev.kord.core.Kord
import dev.kord.core.behavior.interaction.response.respond
import dev.kord.core.event.interaction.GuildChatInputCommandInteractionCreateEvent
import dev.kord.core.on
import dev.kord.rest.builder.interaction.string
import io.ktor.client.request.get
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import me.ghostbear.kumaslash.client
import me.ghostbear.kumaslash.commands.Command
import me.ghostbear.kumaslash.model.Extension
import me.ghostbear.kumaslash.model.toMessage

class SourceCommand : Command {
    override val name: String = "source"
    override val description: String = "Find your missing source based on id"

    override fun register(): suspend Kord.() -> Unit = {
        createGlobalChatInputCommand(name, description) {
            string("id", "Id of the source") {
                required = true
            }
        }

        on<GuildChatInputCommandInteractionCreateEvent> {
            val command = interaction.command
            if (command.rootName != name) return@on
            val response = interaction.deferEphemeralResponse()
            val id = command.strings["id"]!!
            val extensions = extensions.first()

            val extension = extensions.find { extension ->
                extension.sources.find { source ->
                    source.id == id
                } != null
            }

            if (extension != null) {
                response.respond(extension.toMessage())
            } else {
                response.respond { content = "Not found" }
            }
        }
    }
}

val extensions: Flow<List<Extension>> = flow {
    while (true) {
        val raw =
            client.get<String>("https://raw.githubusercontent.com/tachiyomiorg/tachiyomi-extensions/repo/index.min.json") {
                contentType(ContentType.Application.Json)
            }

        emit(Json.decodeFromString(raw))
        delay(3600000)
    }
}
