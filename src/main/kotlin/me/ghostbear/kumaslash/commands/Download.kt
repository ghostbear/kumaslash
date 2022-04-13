package me.ghostbear.kumaslash.commands

import dev.kord.common.entity.ButtonStyle
import dev.kord.common.entity.Choice
import dev.kord.core.Kord
import dev.kord.core.behavior.interaction.respondPublic
import dev.kord.core.behavior.interaction.response.respond
import dev.kord.core.behavior.interaction.updatePublicMessage
import dev.kord.core.entity.interaction.ActionInteraction
import dev.kord.core.entity.interaction.ButtonInteraction
import dev.kord.core.event.interaction.ButtonInteractionCreateEvent
import dev.kord.core.event.interaction.GuildChatInputCommandInteractionCreateEvent
import dev.kord.core.on
import dev.kord.rest.builder.component.ActionRowBuilder
import dev.kord.rest.builder.interaction.string
import io.ktor.client.request.get
import kotlinx.coroutines.delay
import me.ghostbear.kumaslash.client
import me.ghostbear.kumaslash.model.GitHubRelease
import me.ghostbear.kumaslash.model.toMessage
import me.ghostbear.kumaslash.model.updateMessage

private const val NAME = "release"
private const val DESCRIPTION = "Returns download"

enum class RepositoryChoices(val displayName: String, val value: String) {
    NEKO("Neko", "neko"),
    TACHIYOMI_J2K("Tachiyomi J2K", "j2k"),
    TACHIYOMI_SY("Tachiyomi SY", "sy"),
    TACHIYOMI("Tachiyomi", "tachiyomi"),
    UNKNOWN("", "");

    companion object {

        fun fromType(type: String): RepositoryChoices {
            return values().find { it.value == type } ?: UNKNOWN
        }

        fun fromCustomId(customId: String): RepositoryChoices {
            val split = customId.split("_")
            return values().find { it.value == split[0] } ?: UNKNOWN
        }

        val choices: MutableList<Choice<*>>
            get() = values().dropLast(1).map { Choice.StringChoice(it.displayName, it.value) }.toMutableList()
    }
}

enum class ReleaseType {
    NONE,
    STABLE,
    PREVIEW;

    companion object {

        fun fromCustomId(customId: String): ReleaseType {
            val split = customId.split("_")
            return values().find { it.name.equals(split[1], true) } ?: NONE
        }
    }
}

sealed interface Release {
    interface Stable : Release {
        val sOwner: String
        val sRepository: String
    }

    interface Preview : Release {
        val pOwner: String
        val pRepository: String
    }
}

sealed interface Repository {
    object Neko : Repository, Release.Stable {
        override val sOwner: String = "CarlosEsco"
        override val sRepository: String = "Neko"
        override suspend fun message(interaction: ActionInteraction, releaseType: ReleaseType) {
            val response = interaction.deferPublicResponse()
            try {
                val release: GitHubRelease = getRelease()
                response.respond(release.toMessage())
            } catch (e: Exception) {
                e.printStackTrace()
                response.respond {
                    content = "An error occurred, removing message shortly"
                }
                delay(1000)
                response.delete()
            }
        }
    }

    object TachiyomiJ2K : Repository, Release.Stable {
        override val sOwner: String = "Jays2Kings"
        override val sRepository: String = "tachiyomiJ2K"
        override suspend fun message(interaction: ActionInteraction, releaseType: ReleaseType) {
            val response = interaction.deferPublicResponse()
            try {
                val release: GitHubRelease = getRelease()
                response.respond(release.toMessage())
            } catch (e: Exception) {
                e.printStackTrace()
                response.respond {
                    content = "An error occurred, removing message shortly"
                }
                delay(1000)
                response.delete()
            }
        }
    }

    object TachiyomiSY : Repository, Release.Stable, Release.Preview {
        override val sOwner: String = "jobobby04"
        override val sRepository: String = "TachiyomiSY"
        override val pOwner: String = sOwner
        override val pRepository: String = "TachiyomiSYPreview"
        override suspend fun message(interaction: ActionInteraction, releaseType: ReleaseType) {
            when (releaseType) {
                ReleaseType.NONE -> {
                    val response = interaction.deferPublicResponse()
                    response.respond {
                        content = "Which release type do you want"
                        components = components ?: mutableListOf()
                        components?.add(
                            ActionRowBuilder().apply {
                                interactionButton(ButtonStyle.Primary, "sy_stable") {
                                    label = "Stable"
                                }
                                interactionButton(ButtonStyle.Primary, "sy_preview") {
                                    label = "Preview"
                                }
                            }
                        )
                    }
                }
                ReleaseType.STABLE,
                ReleaseType.PREVIEW -> {
                    try {
                        val release: GitHubRelease = getRelease(releaseType)
                        (interaction as ButtonInteraction).updatePublicMessage(release.updateMessage(releaseType == ReleaseType.PREVIEW))
                    } catch (e: Exception) {
                        e.printStackTrace()
                        (interaction as ButtonInteraction).updatePublicMessage {
                            content = "An error occurred, removing message shortly"
                        }
                        delay(1000)
                        interaction.message.delete()
                    }
                }
            }
        }
    }

    object Tachiyomi : Repository, Release.Stable, Release.Preview {
        override val sOwner: String = "tachiyomiorg"
        override val sRepository: String = "tachiyomi"
        override val pOwner: String = sOwner
        override val pRepository: String = "tachiyomi-preview"
        override suspend fun message(interaction: ActionInteraction, releaseType: ReleaseType) {
            when (releaseType) {
                ReleaseType.NONE -> {
                    val response = interaction.deferPublicResponse()
                    response.respond {
                        content = "Which release type do you want"
                        components = components ?: mutableListOf()
                        components?.add(
                            ActionRowBuilder().apply {
                                interactionButton(ButtonStyle.Primary, "tachiyomi_stable") {
                                    label = "Stable"
                                }
                                interactionButton(ButtonStyle.Primary, "tachiyomi_preview") {
                                    label = "Preview"
                                }
                            }
                        )
                    }
                }
                ReleaseType.STABLE,
                ReleaseType.PREVIEW -> {
                    try {
                        val release: GitHubRelease = getRelease(releaseType)
                        (interaction as ButtonInteraction).updatePublicMessage(release.updateMessage(releaseType == ReleaseType.PREVIEW))
                    } catch (e: Exception) {
                        e.printStackTrace()
                        (interaction as ButtonInteraction).updatePublicMessage {
                            content = "An error occurred, removing message shortly"
                        }
                        delay(1000)
                        interaction.message.delete()
                    }
                }
            }
        }
    }

    object Unknown : Repository {
        override suspend fun message(interaction: ActionInteraction, releaseType: ReleaseType) {
            interaction.deferPublicResponse()
            interaction.respondPublic {
                content = "Command not understood"
            }
        }
    }

    suspend fun message(interaction: ActionInteraction, releaseType: ReleaseType)

    companion object {
        fun action(repositoryChoices: RepositoryChoices): Repository {
            return when (repositoryChoices) {
                RepositoryChoices.NEKO -> Neko
                RepositoryChoices.TACHIYOMI_J2K -> TachiyomiJ2K
                RepositoryChoices.TACHIYOMI_SY -> TachiyomiSY
                RepositoryChoices.TACHIYOMI -> Tachiyomi
                RepositoryChoices.UNKNOWN -> Unknown
            }
        }
    }
}

suspend inline fun <reified T> Release.getRelease(releaseType: ReleaseType = ReleaseType.STABLE): T {
    val url = when {
        this is Release.Stable && releaseType == ReleaseType.STABLE -> "https://api.github.com/repos/$sOwner/$sRepository/releases/latest"
        this is Release.Preview && releaseType == ReleaseType.PREVIEW -> "https://api.github.com/repos/$pOwner/$pRepository/releases/latest"
        releaseType == ReleaseType.STABLE -> throw Exception("Release doesn't implement Stable")
        releaseType == ReleaseType.PREVIEW -> throw Exception("Release doesn't implement Preview")
        else -> throw Exception("Release should implement Stable and/or Preview")
    }
    return client.get(url)
}

suspend fun Kord.registerDownloadCommand() {

    createGlobalChatInputCommand(
        NAME,
        DESCRIPTION
    ) {
        string("repository", "The type of Tachiyomi you want") {
            required = true
            choices = RepositoryChoices.choices
        }
    }

    on<GuildChatInputCommandInteractionCreateEvent> {
        val command = interaction.command
        if (command.rootName != NAME) return@on

        val repository = command.strings["repository"]!!
        val repositoryChoices = RepositoryChoices.fromType(repository)

        if (repositoryChoices == RepositoryChoices.UNKNOWN) return@on

        val action = Repository.action(repositoryChoices)
        action.message(interaction, ReleaseType.NONE)
    }

    on<ButtonInteractionCreateEvent> {
        val customId = interaction.component.customId ?: return@on
        val repositoryChoices = RepositoryChoices.fromCustomId(customId)
        val releaseType = ReleaseType.fromCustomId(customId)
        if (repositoryChoices == RepositoryChoices.UNKNOWN || releaseType == ReleaseType.NONE) {
            interaction.message.delete()
            return@on
        }

        val action = Repository.action(repositoryChoices)
        action.message(interaction, releaseType)
    }
}
