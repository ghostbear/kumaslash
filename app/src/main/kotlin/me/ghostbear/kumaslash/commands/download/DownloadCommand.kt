package me.ghostbear.kumaslash.commands.download

import dev.kord.common.entity.Choice
import dev.kord.common.entity.optional.Optional
import dev.kord.core.Kord
import dev.kord.core.behavior.interaction.response.respond
import dev.kord.core.event.interaction.GuildChatInputCommandInteractionCreateEvent
import dev.kord.rest.builder.interaction.string
import dev.kord.rest.builder.message.modify.actionRow
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import me.ghostbear.common.download.Download
import me.ghostbear.common.download.DownloadParams
import me.ghostbear.common.download.Repository
import me.ghostbear.kumaslash.client
import me.ghostbear.kumaslash.util.Env
import me.ghostbear.kumaslash.util.createChatInputCommand
import me.ghostbear.kumaslash.util.on

private const val NAME: String = "download"
private const val DESCRIPTION: String = "Get download link Tachiyomi or supported forks"

suspend fun Kord.downloadCommand() {
    val baseUrl = "http://${Env.url}:${Env.port}"

    createChatInputCommand(NAME, DESCRIPTION) {
        string("repository", "The type of Tachiyomi you want") {
            required = true
            choices = mutableListOf()

            fun choice(name: String, value: Repository) {
                choices?.add(Choice.StringChoice(name, Optional.invoke(), value.name))
            }

            choice("Tachiyomi", Repository.TACHIYOMI)
            choice("TachiyomiSY", Repository.TACHIYOMI_SY)
            choice("TachiyomiJ2K", Repository.TACHIYOMI_J2K)
            choice("Neko", Repository.NEKO)
            choice("Tachiyomi (Preview)", Repository.TACHIYOMI_PREVIEW)
            choice("TachiyomiSY (Preview)", Repository.TACHIYOMI_SY_PREVIEW)
        }
    }
    on<GuildChatInputCommandInteractionCreateEvent>(
        condition = {
            interaction.command.rootName == NAME
        }
    ) {
        val response = interaction.deferPublicResponse()

        val choice = interaction.command.strings["repository"]!!
        val repository = Repository.valueOf(choice)

        val body = client
            .get("$baseUrl/download") {
                contentType(ContentType.Application.Json)
                setBody(
                    DownloadParams(
                        repository = repository
                    )
                )
            }
            .body<Download>()

        response.respond {
            content = body.name

            if (body.repository in listOf(Repository.TACHIYOMI_PREVIEW, Repository.TACHIYOMI_SY_PREVIEW) ) {
                content += "\n\n⚠ Preview is not recommended if you're not willing to test for – and endure – issues. ⚠"
            }

            actionRow {
                linkButton(body.downloadUrl) {
                    label = "Download"
                }
                linkButton(body.changelogUrl) {
                    label = "Changelog"
                }
            }
        }
    }
}
