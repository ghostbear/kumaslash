package me.ghostbear.kumaslash

import dev.kord.core.Kord
import dev.kord.core.entity.application.ApplicationCommand
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.json.serializer.KotlinxSerializer
import kotlinx.coroutines.flow.collectLatest
import kotlinx.serialization.json.Json
import me.ghostbear.kumaslash.commands.Command
import me.ghostbear.kumaslash.commands.download.DownloadCommand
import me.ghostbear.kumaslash.commands.ping.PingCommand
import me.ghostbear.kumaslash.commands.source.SourceCommand
import org.slf4j.LoggerFactory

val client = HttpClient(CIO) {
    install(JsonFeature) {
        serializer = KotlinxSerializer(
            Json {
                ignoreUnknownKeys = true
            }
        )
    }
}

val commands: List<Command> = listOf(
    PingCommand(),
    DownloadCommand(),
    SourceCommand()
)

val logger = LoggerFactory.getLogger("main")

val clean: suspend (ApplicationCommand) -> Unit = clean@{ value ->
    logger.info("Master, I'm currently checking out the command called ${value.name} nya~ (${value.id})")
    val exists = commands.any { command -> command.name == value.name && command.description == value.data.description }
    if (exists) {
        logger.info("Master, the command called ${value.name} is all good nya~")
        return@clean
    }
    logger.warn("Master, I'm removing the command called ${value.name} nya~ (${value.id})")
    value.delete()
}

suspend fun main(args: Array<String>) {
    val kord = Kord(args[0])

    try {
        logger.info("Master, I'm starting to clean the ballroom nya~")
        kord.globalCommands.collectLatest(clean)
    } catch (e: Exception) {
        logger.info("*sobs* Master, I wasn't able to clean the ballroom nya~, e")
    } finally {
        logger.info("Master, I'm done cleaning the ballroom nya~")
    }

    try {
        logger.info("Master, I'm starting to clean the bedrooms nya~")
        kord.guilds
            .collectLatest { guild ->
                logger.info("Master, I'm starting to clean the bedroom (${guild.name}) nya~")
                try {
                    guild.commands.collectLatest(clean)
                } catch (e: Exception) {
                    logger.error("*sobs* Master... I wasn't able to clean the bedroom (${guild.name}) nya~", e)
                }
            }
    } finally {
        logger.info("Master, I'm done cleaning the bedrooms nya~")
    }

    commands.forEach { command ->
        command.register()(kord)
    }

    try {
        kord.login {
            logger.info("$name: Busting the door")
        }
    } catch (e: Exception) {
        logger.error("Wasn't able to start bot", e)
    }
}
