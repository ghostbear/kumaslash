package me.ghostbear.kumaslash.commands

import dev.kord.common.entity.ButtonStyle
import dev.kord.common.entity.Choice
import dev.kord.core.Kord
import dev.kord.core.behavior.interaction.response.respond
import dev.kord.core.behavior.interaction.updatePublicMessage
import dev.kord.core.event.interaction.ButtonInteractionCreateEvent
import dev.kord.core.event.interaction.GuildChatInputCommandInteractionCreateEvent
import dev.kord.core.on
import dev.kord.rest.builder.component.ActionRowBuilder
import dev.kord.rest.builder.interaction.string
import io.ktor.client.request.get
import me.ghostbear.kumaslash.client
import me.ghostbear.kumaslash.model.GitHubRelease
import me.ghostbear.kumaslash.model.toMessage
import me.ghostbear.kumaslash.model.updateMessage

private const val NAME = "release"
private const val DESCRIPTION = "Returns download"

private val SINGLE_RELEASE = listOf(
    Choice.StringChoice("Neko", "Neko"),
    Choice.StringChoice("Tachiyomi J2K", "tachiyomiJ2K"),
)

private val MULTIPLE_RELEASE = listOf(
    Choice.StringChoice("Tachiyomi Sy", "TachiyomiSY"),
    Choice.StringChoice("Tachiyomi", "tachiyomi")
)

enum class Types(val owner: String, val repo: String, val isPreview: Boolean = false) {
    NEKO("CarlosEsco", "Neko"),
    TACHIYOMI_J2K("Jays2Kings", "tachiyomiJ2K"),
    TACHIYOMI_SY("jobobby04", "TachiyomiSY"),
    TACHIYOMI("tachiyomiorg", "tachiyomi"),
    TACHIYOMI_SY_PREVIEW("jobobby04", "TachiyomiSYPreview", true),
    TACHIYOMI_PREVIEW("tachiyomiorg", "tachiyomi-preview", true);

    val url: String
        get() = "https://api.github.com/repos/$owner/$repo/releases/latest"
}

suspend fun Kord.registerDownloadCommand() {

    createGlobalChatInputCommand(
        NAME,
        DESCRIPTION
    ) {
        string("type", "The type of Tachiyomi you want") {
            required = true
            choices = mutableListOf()
            choices?.addAll(SINGLE_RELEASE)
            choices?.addAll(MULTIPLE_RELEASE)
        }
    }

    on<GuildChatInputCommandInteractionCreateEvent> {
        val command = interaction.command
        if (command.rootName != NAME) return@on
        val response = interaction.deferPublicResponse()
        val type = command.strings["type"]!!
        val repos = Types.values().filter { it.repo.startsWith(type) }
        if (repos.size > 1) {
            response.respond {
                // curl \
                //  -H "Accept: application/vnd.github.v3+json" \
                //  https://api.github.com/repos/OWNER/REPO/releases/latest
                content = "Which release type"
                components = mutableListOf()
                components?.add(
                    ActionRowBuilder().apply {
                        interactionButton(ButtonStyle.Primary, "${type}_stable") {
                            label = "Stable"
                        }
                        interactionButton(ButtonStyle.Primary, "${type}_preview") {
                            label = "Preview"
                        }
                    }
                )
            }
        } else {
            val release: GitHubRelease = client.get(repos.first().url)
            response.respond(release.toMessage())
        }
    }

    on<ButtonInteractionCreateEvent> {
        val customId = interaction.component.customId
        println(customId)
        if (listOf("tachiyomi_stable", "tachiyomi_preview", "TachiyomiSY_stable", "TachiyomiSY_preview").contains(customId)) {

            val args = customId?.split("_") ?: return@on
            val isPreview = args[1] == "preview"
            val repo = Types.values().drop(2).find { it.repo.startsWith(args[0]) && it.isPreview == isPreview }
            println("$args | $isPreview | $repo | ${repo?.url}")
            try {
                val release: GitHubRelease = client.get(repo!!.url)
                interaction.updatePublicMessage(release.updateMessage(isPreview))
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
