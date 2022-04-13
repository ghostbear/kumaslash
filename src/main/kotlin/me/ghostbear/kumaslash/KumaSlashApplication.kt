package me.ghostbear.kumaslash

import dev.kord.core.Kord
import dev.kord.core.behavior.interaction.response.respond
import dev.kord.core.event.interaction.GuildChatInputCommandInteractionCreateEvent
import dev.kord.core.on

suspend fun main(args: Array<String>) {
    val kord = Kord(args[0])

    kord.createGlobalChatInputCommand(
        "ping",
        "Returns pong"
    )

    kord.on<GuildChatInputCommandInteractionCreateEvent> {
        val response = interaction.deferEphemeralResponse()
        response.respond {
            content = "Pong"
        }
    }

    kord.login()
}