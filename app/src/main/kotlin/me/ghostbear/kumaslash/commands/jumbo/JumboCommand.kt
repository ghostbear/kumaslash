package me.ghostbear.kumaslash.commands.jumbo

import dev.kord.core.behavior.interaction.respondEphemeral
import dev.kord.core.behavior.interaction.respondPublic
import dev.kord.core.event.interaction.GuildChatInputCommandInteractionCreateEvent
import dev.kord.rest.builder.interaction.OptionsBuilder
import dev.kord.rest.builder.interaction.StringChoiceBuilder
import dev.kord.rest.builder.interaction.string
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.utils.io.jvm.javaio.*
import me.ghostbear.core.OnGuildChatInputCommandInteractionCreateEvent
import me.ghostbear.core.SlashCommand
import me.ghostbear.core.SlashCommandConfig
import me.ghostbear.kumaslash.client

class JumboCommand : SlashCommand(), OnGuildChatInputCommandInteractionCreateEvent {
    override val name: String = "jumbo"
    override val description: String = "Make emotes show their original size"
    override val config: SlashCommandConfig = {
        string("emote", "Emote") {
            required = true
        }
    }

    override fun onGuildChatInputCommandInteractionCreateEvent(): suspend GuildChatInputCommandInteractionCreateEvent.() -> Unit = on@{
        val command = interaction.command
        val emote = command.strings["emote"]!!
        val emoteRegex = Regex("\\<(a)?\\:(.*)\\:(.*)\\>")

        try {
            if (emote.matches(emoteRegex)) {
                val emoteValidator = emoteRegex.find(emote)!!
                val (emoteType, emoteName, emoteId) = emoteValidator.destructured

                val emoteFormat: String = if (emoteType == "a") {
                    "gif"
                } else {
                    "png"
                }

                val imageUrl = "https://cdn.discordapp.com/emojis/${emoteId}.${emoteFormat}?v=1"
                val inputStream = client.get(imageUrl).bodyAsChannel().toInputStream()

                interaction.respondPublic {
                    this.addFile("${emoteName}.${emoteFormat}", inputStream)
                }
            } else {
                interaction.respondEphemeral {
                    content = "Please try again with a single valid emote."
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
