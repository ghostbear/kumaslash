package me.ghostbear.kumaslash.commands.ping

import dev.kord.core.behavior.edit
import dev.kord.core.behavior.interaction.response.respond
import dev.kord.core.event.interaction.GuildChatInputCommandInteractionCreateEvent
import kotlinx.coroutines.delay
import me.ghostbear.core.OnGuildChatInputCommandInteractionCreateEvent
import me.ghostbear.core.SlashCommand
import kotlin.time.ExperimentalTime
import kotlin.time.measureTimedValue

class PingCommand : SlashCommand(), OnGuildChatInputCommandInteractionCreateEvent {
    override val name: String = "ping"
    override val description: String = "Command used to make sure the bot is running"

    @OptIn(ExperimentalTime::class)
    override fun onGuildChatInputCommandInteractionCreateEvent(): suspend GuildChatInputCommandInteractionCreateEvent.() -> Unit = on@{
        if (interaction.command.rootName != name) return@on

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
