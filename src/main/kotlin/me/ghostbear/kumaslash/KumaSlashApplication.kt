package me.ghostbear.kumaslash

import dev.kord.core.Kord
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.json.serializer.KotlinxSerializer
import kotlinx.serialization.json.Json
import me.ghostbear.kumaslash.commands.registerDownloadCommand
import me.ghostbear.kumaslash.commands.registerPingCommand
import me.ghostbear.kumaslash.commands.registerSourceCommand

val client = HttpClient(CIO) {
    install(JsonFeature) {
        serializer = KotlinxSerializer(
            Json {
                ignoreUnknownKeys = true
            }
        )
    }
}
suspend fun main(args: Array<String>) {
    val kord = Kord(args[0])

    kord.registerPingCommand()
    kord.registerSourceCommand()
    kord.registerDownloadCommand()

    kord.login()
}
