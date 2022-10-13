package me.ghostbear.kumaslash

import dev.kord.core.Kord
import dev.kord.core.kordLogger
import dev.kord.gateway.Intent
import dev.kord.gateway.Intents
import dev.kord.gateway.PrivilegedIntent
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.cache.HttpCache
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import me.ghostbear.kumaslash.automation.timeoutReasonAutomation
import me.ghostbear.kumaslash.commands.download.downloadCommand
import me.ghostbear.kumaslash.commands.github.githubCommandGroup
import me.ghostbear.kumaslash.commands.jumbo.jumboCommand
import me.ghostbear.kumaslash.commands.logging.loggingCommand
import me.ghostbear.kumaslash.commands.ping.pingCommand
import me.ghostbear.kumaslash.commands.rules.rulesCommand
import me.ghostbear.kumaslash.commands.source.sourceCommand
import me.ghostbear.kumaslash.commands.steps.requestStepsMessageCommand
import me.ghostbear.kumaslash.commands.steps.stepsCommand
import me.ghostbear.kumaslash.commands.user.userCommandGroup
import me.ghostbear.kumaslash.commands.social.socialCommand
import me.ghostbear.kumaslash.commands.bug.bugCommand
import me.ghostbear.kumaslash.commands.feature.featureCommand
import me.ghostbear.kumaslash.util.Env
import me.ghostbear.kumaslash.util.removeCommands

val json = Json {
    ignoreUnknownKeys = true
    coerceInputValues = true
}

val client = HttpClient(CIO) {
    install(HttpCache)
    install(ContentNegotiation) {
        json(json)
    }
}

@OptIn(PrivilegedIntent::class)
suspend fun main(args: Array<String>) {
    val botToken = Env.botToken ?: throw Exception("No bot token is set")

    val kord = Kord(botToken)

    kord.launch {
        kord.removeCommands()
    }.join()
    kord.pingCommand()
    kord.jumboCommand()
    kord.downloadCommand()
    kord.stepsCommand()
    kord.requestStepsMessageCommand()
    kord.sourceCommand()
    kord.userCommandGroup()
    kord.githubCommandGroup()
    kord.timeoutReasonAutomation()
    kord.rulesCommand()
    kord.loggingCommand()
    kord.socialCommand()
    kord.bugCommand()
    kord.featureCommand()

    try {
        kord.login {
            intents = Intents.nonPrivileged + Intent.GuildMembers
            kordLogger.info("$name: Busting the door")
        }
    } catch (e: Exception) {
        kordLogger.error("Wasn't able to start bot", e)
    }
}
