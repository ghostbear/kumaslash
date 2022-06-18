package me.ghostbear.kumaslash

import dev.kord.common.entity.AuditLogEvent
import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.behavior.channel.createMessage
import dev.kord.core.entity.application.ApplicationCommand
import dev.kord.core.entity.interaction.SubCommand
import dev.kord.core.event.guild.MemberUpdateEvent
import dev.kord.core.event.interaction.ButtonInteractionCreateEvent
import dev.kord.core.event.interaction.GuildChatInputCommandInteractionCreateEvent
import dev.kord.core.event.interaction.GuildMessageCommandInteractionCreateEvent
import dev.kord.core.event.interaction.ModalSubmitInteractionCreateEvent
import dev.kord.core.kordLogger
import dev.kord.core.on
import dev.kord.gateway.Intent
import dev.kord.gateway.Intents
import dev.kord.gateway.PrivilegedIntent
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import java.util.regex.Pattern
import kotlinx.coroutines.flow.collectLatest
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.serialization.json.Json
import me.ghostbear.core.MessageCommand
import me.ghostbear.core.OnButtonInteractionCreateEvent
import me.ghostbear.core.OnGuildChatInputCommandInteractionCreateEvent
import me.ghostbear.core.OnGuildMessageCommandInteractionCreateEvent
import me.ghostbear.core.OnModalSubmitInteractionCreateEvent
import me.ghostbear.core.SlashCommand
import me.ghostbear.core.SlashCommandGroup
import me.ghostbear.kumaslash.commands.download.DownloadCommand
import me.ghostbear.kumaslash.commands.github.GithubCommand
import me.ghostbear.kumaslash.commands.jumbo.JumboCommand
import me.ghostbear.kumaslash.commands.ping.PingCommand
import me.ghostbear.kumaslash.commands.source.SourceCommand
import me.ghostbear.kumaslash.commands.steps.RequestStepsCommand
import me.ghostbear.kumaslash.commands.steps.StepsCommand
import me.ghostbear.kumaslash.commands.user.UserCommand

val json = Json {
    ignoreUnknownKeys = true
    coerceInputValues = true
}

val client = HttpClient(CIO) {
    install(ContentNegotiation) {
        json(json)
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
    SourceCommand(),
    StepsCommand(),
    RequestStepsCommand(),
    JumboCommand(),
    GithubCommand(),
)

@OptIn(PrivilegedIntent::class)
suspend fun main(args: Array<String>) {
    val kord = Kord(args[0])

    var ignoreRole: List<Snowflake>? = null
    if (args.size > 1) {
        try {
            ignoreRole = args[1].split(",").map { Snowflake(it.toLong()) }
            kordLogger.info { "These roles will be ignored: $ignoreRole" }
        } catch (e: Exception) {
            kordLogger.error(e) { "Failed to parse ignore roles" }
        }
    }

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
        if (ignoreRole != null && interaction.user.roleIds.any { roleId -> ignoreRole.any { it.value == roleId.value } }) {
            return@on
        }

        val interactionCommand = interaction.command
        commands.forEach { command ->
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

            if (command is SlashCommand) {
                if (command is OnGuildChatInputCommandInteractionCreateEvent) {
                    if (interaction.command.rootName != command.name) return@forEach
                    command.onGuildChatInputCommandInteractionCreateEvent().invoke(this)
                }
            }
        }
    }

    kord.on<GuildMessageCommandInteractionCreateEvent> {
        if (ignoreRole != null && interaction.user.roleIds.any { roleId -> ignoreRole.any { it.value == roleId.value } }) {
            return@on
        }
        commands.forEach { command ->
            if (command is MessageCommand) {
                if (command is OnGuildMessageCommandInteractionCreateEvent) {
                    if (interaction.invokedCommandName != command.name) return@forEach
                    command.onGuildMessageCommandInteractionCreateEvent().invoke(this)
                }
            }
        }
    }

    kord.on<ButtonInteractionCreateEvent> {
        if (ignoreRole != null && interaction.user.asMember(interaction.getChannel().data.guildId.value!!).roleIds.any { roleId ->
            ignoreRole.any { it.value == roleId.value }
        }
        ) {
            return@on
        }
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
        if (ignoreRole != null && interaction.user.asMember(interaction.message?.getGuild()?.id!!).roleIds.any { roleId -> ignoreRole.any { it.value == roleId.value } }) {
            return@on
        }
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

    kord.on<MemberUpdateEvent> {
        val now = Clock.System.now()
        if (
            member.communicationDisabledUntil != null &&
            old?.communicationDisabledUntil != member.communicationDisabledUntil &&
            member.communicationDisabledUntil!! > now
        ) {
            val auditLogsForUser = kord.rest.auditLog.getAuditLogs(member.guildId) {
                action = AuditLogEvent.MemberUpdate
            }

            val entry = auditLogsForUser
                .auditLogEntries
                .firstOrNull { it.targetId == member.id }

            val epoch = member.communicationDisabledUntil?.epochSeconds
            val reason = entry?.reason?.value?.replace("\n", "\n> ") ?: "No specific reason was given"

            member
                .getDmChannel()
                .createMessage {
                    content = "You've been timed out until <t:$epoch:f> in Tachiyomi with the following reason:\n> $reason"
                }
        }

    }

    try {
        kord.login {
            intents = Intents.nonPrivileged + Intent.GuildMembers
            kordLogger.info("$name: Busting the door")
        }
    } catch (e: Exception) {
        kordLogger.error("Wasn't able to start bot", e)
    }
}
