package me.ghostbear.kumaslash.commands.source

import dev.kord.common.entity.ButtonStyle
import dev.kord.core.Kord
import dev.kord.core.behavior.interaction.response.respond
import dev.kord.core.behavior.interaction.updateEphemeralMessage
import dev.kord.core.event.interaction.ButtonInteractionCreateEvent
import dev.kord.core.event.interaction.GuildChatInputCommandInteractionCreateEvent
import dev.kord.core.kordLogger
import dev.kord.rest.builder.component.ActionRowBuilder
import dev.kord.rest.builder.interaction.string
import dev.kord.rest.builder.message.EmbedBuilder
import dev.kord.rest.builder.message.create.actionRow
import dev.kord.rest.builder.message.create.embed
import dev.kord.rest.builder.message.modify.actionRow
import dev.kord.rest.builder.message.modify.embed
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.http.Url
import me.ghostbear.common.extensions.Match
import me.ghostbear.common.extensions.Source
import me.ghostbear.kumaslash.client
import me.ghostbear.kumaslash.util.createChatInputCommand
import me.ghostbear.kumaslash.util.on

private const val NAME: String = "source"
private const val DESCRIPTION: String = "Find your missing source based on id"

suspend fun Kord.sourceCommand() {
    fun createEmbed(match: Match<Source>): EmbedBuilder.() -> Unit = {
        thumbnail {
            kordLogger.info { "https://raw.githubusercontent.com/tachiyomiorg/tachiyomi-extensions/repo/icon/${match.value.apk.substringBeforeLast(".")}.png" }
            url = "https://raw.githubusercontent.com/tachiyomiorg/tachiyomi-extensions/repo/icon/${match.value.apk.substringBeforeLast(".")}.png"
        }
        title = match.value.name.replace("Tachiyomi: ", "")
        description = """
            Match: ${match.highlight}
            Language: ${match.value.lang}
            Version: ${match.value.version}
        """.trimIndent()
    }
    fun createPagingActionRow(id: String, index: Int, size: Int): ActionRowBuilder.() -> Unit = {
        interactionButton(ButtonStyle.Primary, "prev?id=${id}&index=${index - 1}") {
            label = "Prev"
            disabled = index == 0
        }
        interactionButton(ButtonStyle.Secondary, "nothing") {
            label = "${index + 1}/${size}"
            disabled = true
        }
        interactionButton(ButtonStyle.Primary, "next?id=${id}&index=${index + 1}") {
            label = "Next"
            disabled = index == size - 1
        }
    }
    fun createActionRow(match: Match<Source>): ActionRowBuilder.() -> Unit = {
        linkButton("https://raw.githubusercontent.com/tachiyomiorg/tachiyomi-extensions/repo/apk/${match.value.apk}") {
            label = "Download"
        }
    }
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

        val raw = client.get("http://127.0.0.1:8080/extension/$id")
        val matches = raw.body<List<Match<Source>>>()

        if (matches.isEmpty()) {
            response.respond {
                embed {
                    title = "No sources found with that **id**"
                    description = "There was no source found with that **id**. This could be because either it doesn't exists or that it has been removed due to being to hard to maintain or the scanlator asked to be removed."
                }
            }
            return@on
        }

        val index = 0
        val match = matches[0]

        response.respond {
            embed(createEmbed(match))
            actionRow(createPagingActionRow(id, index, matches.size))
            actionRow(createActionRow(match))
        }
    }
    on<ButtonInteractionCreateEvent>(
        condition = {
            interaction.componentId.startsWith("prev") || interaction.componentId.startsWith("next")
        }
    ) {
        val uri = Url(interaction.componentId)
        val id = uri.parameters["id"]!!
        val index = uri.parameters["index"]!!.toInt()

        val raw = client.get("http://127.0.0.1:8080/extension/$id")
        val matches = raw.body<List<Match<Source>>>()
        val match = matches[index]

        interaction.updateEphemeralMessage {
            embed(createEmbed(match))
            actionRow(createPagingActionRow(id, index, matches.size))
            actionRow(createActionRow(match))
        }
    }
}
