package me.ghostbear.kumaslash.util

import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.event.Event
import dev.kord.core.kordLogger
import dev.kord.rest.builder.interaction.ChatInputCreateBuilder
import dev.kord.rest.builder.interaction.GlobalChatInputCreateBuilder
import dev.kord.rest.builder.interaction.GlobalMessageCommandCreateBuilder
import dev.kord.rest.builder.interaction.MessageCommandCreateBuilder
import dev.kord.rest.builder.interaction.SubCommandBuilder
import dev.kord.rest.builder.interaction.subCommand
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

typealias SubCommandInitializer = (String, String, SubCommandBuilder.() -> Unit) -> Unit

val GlobalChatInputCreateBuilder.subCommandInitializer: SubCommandInitializer
    get() = { name, description, builder ->
        subCommand(name, description, builder)
    }

inline fun <reified T : Event> Kord.on(
    scope: CoroutineScope = this,
    crossinline condition: T.() -> Boolean,
    noinline consumer: suspend T.() -> Unit
): Job =
    events.buffer(Channel.UNLIMITED).filterIsInstance<T>()
        .onEach { event ->
            scope.launch(event.coroutineContext) {
                runCatching {
                    if (condition(event)) {
                        consumer(event)
                    }
                }.onFailure {
                    kordLogger.catching(it)
                }
            }
        }
        .launchIn(scope)

val commandHashCodes = mutableListOf<Int>()

fun hashCode(name: String, description: String): Int {
    var result = name.hashCode()
    result = 31 * result + description.hashCode()
    return result
}

suspend inline fun Kord.createChatInputCommand(
    name: String,
    description: String = "",
    noinline builder: GlobalChatInputCreateBuilder.() -> Unit = {}
) {
    val guildId = Env.guildId
    if (Env.debug == true && guildId != null) {
        kordLogger.info { "Creating a guild command with name \"$name\" and description \"$description\"" }
        createGuildChatInputCommand(
            Snowflake(guildId),
            name,
            description,
            (builder as ChatInputCreateBuilder.() -> Unit)
        )
    } else {
        kordLogger.info { "Creating a global command with name \"$name\" and description \"$description\"" }
        createGlobalChatInputCommand(name, description, builder)
    }
    commandHashCodes.add(hashCode(name, description))
}

suspend inline fun Kord.createMessageCommand(
    name: String,
    noinline builder: GlobalMessageCommandCreateBuilder.() -> Unit = {}
) {
    val guildId = Env.guildId
    if (Env.debug == true && guildId != null) {
        kordLogger.info { "Creating a guild command with name \"$name\"" }
        createGuildMessageCommand(Snowflake(guildId), name, builder as MessageCommandCreateBuilder.() -> Unit)
    } else {
        kordLogger.info { "Creating a global message command with name \"$name\"" }
        createGlobalMessageCommand(name, builder)
    }
}

suspend fun Kord.removeCommands() {
    getGlobalApplicationCommands()
        .collectLatest { globalApplicationCommand ->
            globalApplicationCommand.delete()
        }
    guilds.collectLatest { guild ->
        guild.getApplicationCommands()
            .collectLatest guild@{ guildApplicationCommand ->
                guildApplicationCommand.delete()
            }
    }
    kordLogger.info { "Removed commands" }
}