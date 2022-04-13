package me.ghostbear.kumaslash

import dev.kord.core.Kord
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*

import me.ghostbear.kumaslash.commands.registerPingCommand
import me.ghostbear.kumaslash.commands.registerSourceCommand

val client = HttpClient(CIO) {
    install(JsonFeature) {
        serializer = KotlinxSerializer(kotlinx.serialization.json.Json {
            ignoreUnknownKeys = true
        })
    }
}
suspend fun main(args: Array<String>) {
    val kord = Kord(args[0])

    kord.registerPingCommand()
    kord.registerSourceCommand()

    kord.login()
}