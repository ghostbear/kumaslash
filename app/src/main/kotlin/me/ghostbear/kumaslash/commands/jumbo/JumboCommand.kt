package me.ghostbear.kumaslash.commands.jumbo

import dev.kord.core.Kord
import dev.kord.core.behavior.interaction.respondEphemeral
import dev.kord.core.behavior.interaction.response.respond
import dev.kord.core.event.interaction.GuildChatInputCommandInteractionCreateEvent
import dev.kord.rest.Image
import dev.kord.rest.builder.interaction.string
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsChannel
import io.ktor.utils.io.jvm.javaio.toInputStream
import java.io.InputStream
import me.ghostbear.kumaslash.client
import me.ghostbear.kumaslash.util.createChatInputCommand
import me.ghostbear.kumaslash.util.isAnimated
import me.ghostbear.kumaslash.util.on

private const val NAME: String = "jumbo"
private const val DESCRIPTION: String = "Make emotes show their original size"

private val EMOJI_PATTERN = Regex("\\<(a)?\\:(.*)\\:(.*)\\>")

suspend fun Kord.jumboCommand() {
    createChatInputCommand(NAME, DESCRIPTION) {
        string("emote", "Emote") {
            required = true
        }
    }
    on<GuildChatInputCommandInteractionCreateEvent>(
        condition = {
            interaction.command.rootName == NAME
        },
    ) {
        val emote = interaction.command.strings["emote"]!!
        if (emote.matches(EMOJI_PATTERN).not()) {
            interaction.respondEphemeral {
                content = "Please try again with a single valid emote."
            }
            return@on
        }
        val response = interaction.deferPublicResponse()

        val emoteValidator = EMOJI_PATTERN.find(emote)!!
        val (emoteType, emoteName, emoteId) = emoteValidator.destructured

        val emoteFormat = if (emoteType.isAnimated()) Image.Format.GIF else Image.Format.PNG

        val data = getEmoji(emoteId, emoteFormat)

        response.respond {
            addFile("${emoteName}.${emoteFormat.extension}", data)
        }
    }
}

private suspend fun getEmoji(id: String, format: Image.Format): InputStream =
    client.get("https://cdn.discordapp.com/emojis/${id}.${format.extension}?v=1")
        .bodyAsChannel()
        .toInputStream()
