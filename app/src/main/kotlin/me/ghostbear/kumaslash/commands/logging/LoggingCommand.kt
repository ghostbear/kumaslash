package me.ghostbear.kumaslash.commands.logging

import dev.kord.common.DiscordBitSet
import dev.kord.common.entity.Permissions
import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.behavior.interaction.response.respond
import dev.kord.core.entity.channel.TextChannel
import dev.kord.core.entity.interaction.SubCommand
import dev.kord.core.event.interaction.GuildChatInputCommandInteractionCreateEvent
import dev.kord.core.kordLogger
import dev.kord.core.supplier.EntitySupplyStrategy
import dev.kord.rest.builder.interaction.channel
import dev.kord.rest.builder.interaction.string
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import me.ghostbear.common.logging.Log
import me.ghostbear.common.logging.Logging
import me.ghostbear.kumaslash.client
import me.ghostbear.kumaslash.util.Env
import me.ghostbear.kumaslash.util.SubCommandInitializer
import me.ghostbear.kumaslash.util.createChatInputCommand
import me.ghostbear.kumaslash.util.on
import me.ghostbear.kumaslash.util.subCommandInitializer

private val NAME: String = "logging"
private val DESCRIPTION: String = "Add and remove logging channels"

suspend fun Kord.loggingCommand() {
    createChatInputCommand(NAME, DESCRIPTION) {
        defaultMemberPermissions = Permissions(DiscordBitSet(0))
        getLoggingCommand(subCommandInitializer)
        setLoggingCommand(subCommandInitializer)
        deleteLoggingCommand(subCommandInitializer)
    }
}

fun Kord.getLoggingCommand(initializer: SubCommandInitializer) {
    val baseUrl = "http://${Env.url}:${Env.port}"
    initializer("get", "Get logging channel for the Guild by Log type") {
        string("type", "The log type") {
            choice("Mod", Log.MOD.name)
            required = true
        }
    }
    on<GuildChatInputCommandInteractionCreateEvent>(
        condition = condition@{
            if (interaction.command is SubCommand) {
                return@condition (interaction.command as SubCommand).name == "get"
            }
            false
        }
    ) {
        val command = interaction.command

        val type = command.strings["type"]!!

        val response = interaction.deferPublicResponse()

        try {
            val request = client.get("$baseUrl/logging") {
                contentType(ContentType.Application.Json)
                setBody(
                    Logging(
                        Log.valueOf(type),
                        interaction.guildId.value.toLong(),
                        0
                    )
                )
            }

            if (request.status == HttpStatusCode.NotFound) {
                throw NotFoundException("Channel not found")
            } else {
                val logging = request.body<Logging>()
                val channel = logging.toTextChannel(kord)
                response.respond {
                    content = "$type is logging to ${channel.mention}"
                }
            }
        } catch (e: NotFoundException) {
            kordLogger.error(t = e, ) { "Logging channel not found" }
            response.respond {
                content = "$type doesn't have a logging channel in this Guild"
            }
        } catch (e: Exception) {
            kordLogger.error(t = e, ) { "Error getting logging channel" }
            response.respond {
                content = "Failed to get logging channel for $type"
            }
        }
    }
}

fun Kord.setLoggingCommand(initializer: SubCommandInitializer) {
    val baseUrl = "http://${Env.url}:${Env.port}"
    initializer("set", "Set logging channel for the Guild by Log type") {
        string("type", "The log type") {
            choice("Mod", Log.MOD.name)
            required = true
        }
        channel("channel", "Channel to log to") {
            required = true
        }
    }
    on<GuildChatInputCommandInteractionCreateEvent>(
        condition = condition@{
            if (interaction.command is SubCommand) {
                return@condition (interaction.command as SubCommand).name == "set"
            }
            false
        }
    ) {
        val command = interaction.command

        val type = command.strings["type"]!!
        val channel = command.channels["channel"]!!

        val response = interaction.deferPublicResponse()

        try {
            val request = client.post("$baseUrl/logging") {
                contentType(ContentType.Application.Json)
                setBody(
                    Logging(
                        Log.valueOf(type),
                        interaction.guildId.value.toLong(),
                        channel.id.value.toLong()
                    )
                )
            }

            if (request.status == HttpStatusCode.OK) {
                response.respond {
                    content = "$type is now logging to ${channel.mention}"
                }
            } else {
                throw Exception("Not OK")
            }
        } catch (e: Exception) {
            kordLogger.error(t = e, ) { "Error setting logging channel" }
            response.respond {
                content = "Failed to set logging channel for $type"
            }
        }
    }
}

fun Kord.deleteLoggingCommand(initializer: SubCommandInitializer) {
    val baseUrl = "http://${Env.url}:${Env.port}"
    initializer("delete", "Delete logging channel for the Guild by Log type") {
        string("type", "The log type") {
            choice("Mod", Log.MOD.name)
            required = true
        }
    }
    on<GuildChatInputCommandInteractionCreateEvent>(
        condition = condition@{
            if (interaction.command is SubCommand) {
                return@condition (interaction.command as SubCommand).name == "delete"
            }
            false
        }
    ) {
        val command = interaction.command

        val type = command.strings["type"]!!

        val response = interaction.deferPublicResponse()

        try {
            val request = client.delete("$baseUrl/logging") {
                contentType(ContentType.Application.Json)
                setBody(
                    Logging(
                        Log.valueOf(type),
                        interaction.guildId.value.toLong(),
                        0
                    )
                )
            }

            if (request.status == HttpStatusCode.OK) {
                response.respond {
                    content = "$type no longer has a channel to log to"
                }
            } else {
                throw Exception("Not OK")
            }
        } catch (e: Exception) {
            kordLogger.error(t = e, ) { "Error deleting logging channel" }
            response.respond {
                content = "Failed to delete logging channel for $type"
            }
        }
    }
}

suspend fun Logging.toTextChannel(
    kord: Kord,
    strategy: EntitySupplyStrategy<*> = EntitySupplyStrategy.rest
): TextChannel {
    val channel = kord.getChannel(Snowflake(channelId), strategy) ?: throw NotFoundException("Channel not found")
    return TextChannel(channel.data, kord, kord.defaultSupplier)
}

class NotFoundException(override val message: String) : Exception(message)