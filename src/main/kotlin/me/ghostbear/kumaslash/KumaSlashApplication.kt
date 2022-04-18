package me.ghostbear.kumaslash

import dev.kord.core.Kord
import dev.kord.core.entity.application.ApplicationCommand
import dev.kord.core.entity.interaction.SubCommand
import dev.kord.core.event.interaction.ButtonInteractionCreateEvent
import dev.kord.core.event.interaction.GuildChatInputCommandInteractionCreateEvent
import dev.kord.core.event.interaction.ModalSubmitInteractionCreateEvent
import dev.kord.core.kordLogger
import dev.kord.core.on
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.json.serializer.KotlinxSerializer
import kotlinx.coroutines.flow.collectLatest
import kotlinx.serialization.json.Json
import me.ghostbear.kumaslash.commands.base.OnButtonInteractionCreateEvent
import me.ghostbear.kumaslash.commands.base.OnGuildChatInputCommandInteractionCreateEvent
import me.ghostbear.kumaslash.commands.base.OnModalSubmitInteractionCreateEvent
import me.ghostbear.kumaslash.commands.base.SlashCommandGroup
import me.ghostbear.kumaslash.commands.download.DownloadCommand
import me.ghostbear.kumaslash.commands.ping.PingCommand
import me.ghostbear.kumaslash.commands.source.SourceCommand
import me.ghostbear.kumaslash.commands.user.UserCommand

val client = HttpClient(CIO) {
    install(JsonFeature) {
        serializer = KotlinxSerializer(
            Json {
                ignoreUnknownKeys = true
            }
        )
    }
}

val clean: suspend (ApplicationCommand) -> Unit = clean@{ value ->
    kordLogger.info("Master, I'm currently checking out the command called ${value.name} nya~ (${value.id})")
    val exists = commands.any { command -> command.name == value.name && command.description == value.data.description }
    if (exists) {
        kordLogger.info("Master, the command called ${value.name} is all good nya~")
        return@clean
    }
    kordLogger.warn("Master, I'm removing the command called ${value.name} nya~ (${value.id})")
    value.delete()
}

val commands = mutableListOf(
    PingCommand(),
    UserCommand(),
    DownloadCommand(),
    SourceCommand()
)

suspend fun main(args: Array<String>) {
    val kord = Kord(args[0])

    try {
        kordLogger.info("Master, I'm starting to clean the ballroom nya~")
        kord.globalCommands.collectLatest(clean)
    } catch (e: Exception) {
        kordLogger.info("*sobs* Master, I wasn't able to clean the ballroom nya~, e")
    } finally {
        kordLogger.info("Master, I'm done cleaning the ballroom nya~")
    }

    try {
        kordLogger.info("Master, I'm starting to clean the bedrooms nya~")
        kord.guilds
            .collectLatest { guild ->
                kordLogger.info("Master, I'm starting to clean the bedroom (${guild.name}) nya~")
                try {
                    guild.commands.collectLatest(clean)
                } catch (e: Exception) {
                    kordLogger.error("*sobs* Master... I wasn't able to clean the bedroom (${guild.name}) nya~", e)
                }
            }
    } finally {
        kordLogger.info("Master, I'm done cleaning the bedrooms nya~")
    }

    commands.forEach { command ->
        command.register().invoke(kord)
    }

    kord.on<GuildChatInputCommandInteractionCreateEvent> {
        val interactionCommand = interaction.command
        commands
            .forEach { command ->
                if (interactionCommand is SubCommand) {
                    if (command !is SlashCommandGroup) return@forEach
                    command.subcommands.forEach subcommands@{ subcommand ->
                        if (interactionCommand.name != subcommand.name) return@subcommands
                        if (subcommand is OnGuildChatInputCommandInteractionCreateEvent) {
                            subcommand.onGuildChatInputCommandInteractionCreateEvent().invoke(this)
                            return@subcommands
                        }
                    }
                    return@forEach
                }

                if (command is OnGuildChatInputCommandInteractionCreateEvent) {
                    if (interaction.command.rootName != command.name) return@forEach
                    command.onGuildChatInputCommandInteractionCreateEvent().invoke(this)
                }
            }
    }

    kord.on<ButtonInteractionCreateEvent> {
        commands
            .flatMap {
                if (it is SlashCommandGroup) {
                    return@flatMap it.subcommands
                }
                return@flatMap listOf(it)
            }
            .forEach { command ->
                if (command is OnButtonInteractionCreateEvent) {
                    command.onButtonInteractionCreateEvent().invoke(this)
                }
            }
    }

    kord.on<ModalSubmitInteractionCreateEvent> {
        commands
            .flatMap {
                if (it is SlashCommandGroup) {
                    return@flatMap it.subcommands
                }
                return@flatMap listOf(it)
            }
            .forEach { command ->
                if (command is OnModalSubmitInteractionCreateEvent) {
                    command.onModalSubmitInteractionCreateEvent().invoke(this)
                }
            }
    }

    try {
        kord.login {
            kordLogger.info("$name: Busting the door")
        }
    } catch (e: Exception) {
        kordLogger.error("Wasn't able to start bot", e)
    }
}
