package me.ghostbear.kumaslash.commands.ping

import dev.kord.core.Kord
import dev.kord.core.behavior.edit
import dev.kord.core.behavior.interaction.response.respond
import dev.kord.core.event.interaction.GuildChatInputCommandInteractionCreateEvent
import kotlin.time.ExperimentalTime
import kotlin.time.measureTimedValue
import kotlinx.coroutines.delay
import me.ghostbear.kumaslash.util.createChatInputCommand
import me.ghostbear.kumaslash.util.on

private const val NAME = "ping"
private const val DESCRIPTION = "Command used to make sure the bot is running"

@OptIn(ExperimentalTime::class)
suspend fun Kord.pingCommand() {
    createChatInputCommand(NAME, DESCRIPTION)
    on<GuildChatInputCommandInteractionCreateEvent>(
        condition = {
            interaction.command.rootName == NAME
        }
    ) {
        val response = interaction.deferPublicResponse()
        val (value, duration) = measureTimedValue {
            response.respond {
                content = "Pong"
            }
        }

        value.message.edit {
            content = "Pong took $duration"
        }

        delay(1000)

        value.message.delete()
    }
}
