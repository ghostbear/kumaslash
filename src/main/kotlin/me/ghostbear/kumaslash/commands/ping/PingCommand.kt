package me.ghostbear.kumaslash.commands.ping

import dev.kord.core.Kord
import dev.kord.core.behavior.edit
import dev.kord.core.behavior.interaction.response.respond
import dev.kord.core.event.interaction.GuildChatInputCommandInteractionCreateEvent
import dev.kord.core.on
import kotlinx.coroutines.delay
import kotlin.time.ExperimentalTime
import kotlin.time.measureTimedValue
import me.ghostbear.kumaslash.commands.Command

class PingCommand : Command {
    override val name: String = "ping"
    override val description: String = "Command used to make sure the bot is running"

    @OptIn(ExperimentalTime::class)
    override fun register(): suspend Kord.() -> Unit = {
        createGlobalChatInputCommand(name, description)

        on<GuildChatInputCommandInteractionCreateEvent> {
            if (interaction.command.rootName != name) return@on

            val response = interaction.deferPublicResponse()
            val timedValue = measureTimedValue {
                response.respond {
                    content = "Pong"
                }
            }

            timedValue.value.message.edit {
                content = "Pong took ${timedValue.duration}"
            }

            delay(1000)

            timedValue.value.message.delete()
        }
    }
}
