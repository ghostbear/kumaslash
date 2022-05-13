package me.ghostbear.kumaslash.commands.source

import dev.kord.core.behavior.interaction.response.respond
import dev.kord.core.event.interaction.GuildChatInputCommandInteractionCreateEvent
import dev.kord.rest.builder.interaction.OptionsBuilder
import dev.kord.rest.builder.interaction.StringChoiceBuilder
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
import kotlinx.serialization.json.Json
import me.ghostbear.core.OnGuildChatInputCommandInteractionCreateEvent
import me.ghostbear.core.SlashCommand
import me.ghostbear.core.SlashCommandConfig
import me.ghostbear.data.tachiyomi.Extension
import me.ghostbear.data.tachiyomi.toMessage
import me.ghostbear.kumaslash.client

class SourceCommand : SlashCommand(), OnGuildChatInputCommandInteractionCreateEvent {
    override val name: String = "source"
    override val description: String = "Find your missing source based on id"
    override val config: SlashCommandConfig = {
        string("id", "Id of the source") {
            required = true
        }
    }

    override fun onGuildChatInputCommandInteractionCreateEvent(): suspend GuildChatInputCommandInteractionCreateEvent.() -> Unit = on@{
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

val extensions: Flow<List<Extension>> = flow {
    while (true) {
        val raw =
            client.get("https://raw.githubusercontent.com/tachiyomiorg/tachiyomi-extensions/repo/index.min.json") {
                contentType(ContentType.Application.Json)
            }

        emit(Json.decodeFromString(raw.bodyAsText()))
        delay(3600000)
    }
}
