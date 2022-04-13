package me.ghostbear.kumaslash.commands

import dev.kord.core.Kord
import dev.kord.core.behavior.interaction.response.respond
import dev.kord.core.event.interaction.GuildChatInputCommandInteractionCreateEvent
import dev.kord.core.on

suspend fun Kord.registerPingCommand() {
    createGlobalChatInputCommand(
        "ping",
        "Returns pong"
    )

    on<GuildChatInputCommandInteractionCreateEvent> {
        val response = interaction.deferEphemeralResponse()
        response.respond {
            content = "Pong"
        }
    }
}