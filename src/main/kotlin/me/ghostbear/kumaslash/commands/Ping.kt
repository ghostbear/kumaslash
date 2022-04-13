package me.ghostbear.kumaslash.commands

import dev.kord.core.Kord
import dev.kord.core.behavior.interaction.response.respond
import dev.kord.core.event.interaction.GuildChatInputCommandInteractionCreateEvent
import dev.kord.core.on

private const val NAME = "ping"
private const val DESCRIPTION = "Returns pong"

suspend fun Kord.registerPingCommand() {

    createGlobalChatInputCommand(
        NAME,
        DESCRIPTION
    )

    on<GuildChatInputCommandInteractionCreateEvent> {
        if (interaction.command.rootName != NAME) return@on
        val response = interaction.deferEphemeralResponse()
        response.respond {
            content = "Pong"
        }
    }

}