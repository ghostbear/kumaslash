package me.ghostbear.kumaslash.commands.download

import dev.kord.common.entity.ButtonStyle
import dev.kord.common.entity.Choice
import dev.kord.common.entity.optional.Optional
import dev.kord.core.behavior.interaction.response.DeferredPublicMessageInteractionResponseBehavior
import dev.kord.core.behavior.interaction.response.respond
import dev.kord.core.behavior.interaction.updatePublicMessage
import dev.kord.core.entity.interaction.ActionInteraction
import dev.kord.core.entity.interaction.ButtonInteraction
import dev.kord.core.entity.interaction.ChatInputCommandInteraction
import dev.kord.core.event.interaction.ButtonInteractionCreateEvent
import dev.kord.core.event.interaction.GuildChatInputCommandInteractionCreateEvent
import dev.kord.rest.builder.component.ActionRowBuilder
import dev.kord.rest.builder.interaction.OptionsBuilder
import dev.kord.rest.builder.interaction.StringChoiceBuilder
import dev.kord.rest.builder.interaction.string
import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlinx.coroutines.delay
import me.ghostbear.core.OnButtonInteractionCreateEvent
import me.ghostbear.core.OnGuildChatInputCommandInteractionCreateEvent
import me.ghostbear.core.SlashCommand
import me.ghostbear.core.SlashCommandConfig
import me.ghostbear.data.github.GitHubRelease
import me.ghostbear.data.github.toMessage
import me.ghostbear.data.github.updateMessage
import me.ghostbear.kumaslash.client

interface Strategy {
    suspend fun execute(interaction: ActionInteraction, repository: Repository)
}

class ChoiceStrategy : Strategy {
    override suspend fun execute(interaction: ActionInteraction, repository: Repository) {
        val response = interaction.deferPublicResponse()
        response.respond {
            content = "Which release type do you want"
            components = components ?: mutableListOf()
            components?.add(
                ActionRowBuilder().apply {
                    interactionButton(ButtonStyle.Primary, "${repository.owner}_stable") {
                        label = "Stable"
                    }
                    interactionButton(ButtonStyle.Primary, "${repository.owner}_preview") {
                        label = "Preview"
                    }
                }
            )
        }
    }
}

class DisplayStrategy : Strategy {
    override suspend fun execute(interaction: ActionInteraction, repository: Repository) {
        var response: DeferredPublicMessageInteractionResponseBehavior? = null
        try {
            val release: GitHubRelease = client.get(repository.url).body()
            when (interaction) {
                is ChatInputCommandInteraction -> {
                    response = interaction.deferPublicResponse()
                    response.respond(release.toMessage())
                }
                is ButtonInteraction -> {
                    interaction.updatePublicMessage(release.updateMessage(repository.isPreview))
                }
                else -> throw Exception("Unsupported ActionInteraction")
            }
        } catch (e: Exception) {
            e.printStackTrace()
            when (interaction) {
                is ChatInputCommandInteraction -> {
                    response = response.run {
                        this ?: interaction.deferPublicResponse()
                    }
                    response.respond {
                        content = "An error occurred, removing message shortly"
                    }
                    delay(1000)
                    response.delete()
                }
                is ButtonInteraction -> {
                    interaction.updatePublicMessage {
                        content = "An error occurred, removing message shortly"
                    }
                    delay(1000)
                    interaction.message.delete()
                }
                else -> throw Exception("Unsupported ActionInteraction")
            }
        }
    }
}

class Context {
    lateinit var strategy: Strategy

    suspend fun executeStrategy(interaction: ActionInteraction, repository: Repository) {
        strategy.execute(interaction, repository)
    }
}

enum class Repository(
    val owner: String,
    val repo: String,
    val choice: Choice<*>? = null,
    val isPreview: Boolean = false
) {
    TACHIYOMI(
        owner = "tachiyomiorg",
        repo = "tachiyomi",
        choice = Choice.StringChoice("Tachiyomi", Optional.invoke(), "tachiyomi")
    ),
    TACHIYOMI_PREVIEW(
        owner = "tachiyomiorg",
        repo = "tachiyomi-preview",
        isPreview = true
    ),
    NEKO(
        owner = "CarlosEsco",
        repo = "Neko",
        choice = Choice.StringChoice("Neko", Optional.invoke(), "neko")
    ),
    TACHIYOMI_J2K(
        owner = "Jays2Kings",
        repo = "tachiyomiJ2K",
        choice = Choice.StringChoice("Tachiyomi J2K", Optional.invoke(), "j2k")
    ),
    TACHIYOMI_SY(
        owner = "jobobby04",
        repo = "TachiyomiSY",
        choice = Choice.StringChoice("Tachiyomi SY", Optional.invoke(), "sy")
    ),
    TACHIYOMI_SY_PREVIEW(
        owner = "jobobby04",
        repo = "TachiyomiSYPreview",
        isPreview = true
    );

    val url: String
        get() = "https://api.github.com/repos/$owner/$repo/releases/latest"

    companion object {
        fun parseCustomId(customId: String): Repository {
            val split = customId.split("_")
            val owner = split[0]
            val isPreview = split[1].contains("preview", true)
            return when {
                owner == NEKO.owner -> NEKO
                owner == TACHIYOMI_J2K.owner -> TACHIYOMI_J2K
                owner == TACHIYOMI_SY.owner && isPreview -> TACHIYOMI_SY_PREVIEW
                owner == TACHIYOMI_SY.owner -> TACHIYOMI_SY
                owner == TACHIYOMI.owner && isPreview -> TACHIYOMI_PREVIEW
                owner == TACHIYOMI.owner -> TACHIYOMI
                else -> throw Exception("Unknown repository")
            }
        }

        fun parseChoice(choice: String): Repository {
            return when (choice) {
                NEKO.choice!!.value -> NEKO
                TACHIYOMI_J2K.choice!!.value -> TACHIYOMI_J2K
                TACHIYOMI_SY.choice!!.value -> TACHIYOMI_SY
                TACHIYOMI.choice!!.value -> TACHIYOMI
                else -> throw Exception("Unknown repository")
            }
        }

        val choices: MutableList<Choice<*>>
            get() = values().mapNotNull { it.choice }.toMutableList()
    }
}

class DownloadCommand : SlashCommand(), OnGuildChatInputCommandInteractionCreateEvent, OnButtonInteractionCreateEvent {
    override val name: String = "download"
    override val description: String = "Get download link Tachiyomi or supported forks"
    override val config: SlashCommandConfig = {
        string("repository", "The type of Tachiyomi you want") {
            required = true
            choices = Repository.choices
        }
    }

    override fun onButtonInteractionCreateEvent(): suspend ButtonInteractionCreateEvent.() -> Unit = on@{
        val customId = interaction.component.customId ?: return@on
        if (!listOf("stable", "preview").any { customId.endsWith(it) }) return@on

        val context = Context()
        context.strategy = DisplayStrategy()

        val repository = Repository.parseCustomId(customId)

        context.executeStrategy(interaction, repository)
    }

    override fun onGuildChatInputCommandInteractionCreateEvent(): suspend GuildChatInputCommandInteractionCreateEvent.() -> Unit = on@{
        val command = interaction.command
        if (command.rootName != name) return@on

        val context = Context()

        val choice = command.strings["repository"]!!
        val repository = Repository.parseChoice(choice)

        when (repository) {
            Repository.NEKO,
            Repository.TACHIYOMI_J2K -> context.strategy = DisplayStrategy()
            Repository.TACHIYOMI_SY,
            Repository.TACHIYOMI -> context.strategy = ChoiceStrategy()
        }

        context.executeStrategy(interaction, repository)
    }
}
