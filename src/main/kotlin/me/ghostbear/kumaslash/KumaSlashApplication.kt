package me.ghostbear.kumaslash

import dev.kord.core.Kord
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.json.serializer.KotlinxSerializer
import kotlinx.serialization.json.Json
import me.ghostbear.kumaslash.commands.Command
import me.ghostbear.kumaslash.commands.download.DownloadCommand
import me.ghostbear.kumaslash.commands.ping.PingCommand
import me.ghostbear.kumaslash.commands.source.SourceCommand

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

suspend fun main(args: Array<String>) {
    val kord = Kord(args[0])

    commands.forEach { command ->
        command.register()(kord)
    }

    kord.login()
}
