package me.ghostbear.kumaslash.commands.github

import com.haroldadmin.opengraphKt.getOpenGraphTags
import dev.kord.core.behavior.interaction.response.respond
import dev.kord.core.entity.interaction.InteractionCommand
import dev.kord.core.event.interaction.GuildChatInputCommandInteractionCreateEvent
import dev.kord.rest.builder.interaction.string
import java.net.URL
import kotlinx.coroutines.delay
import me.ghostbear.core.OnGuildChatInputCommandInteractionCreateEvent
import me.ghostbear.core.SubSlashCommand
import me.ghostbear.core.SubSlashCommandConfig

internal const val OWNER = "tachiyomiorg"
internal const val ARG_REPOSITORY = "repository"
internal const val ARG_NUMBER = "number"

fun InteractionCommand.getArguments(): Pair<String, String?> {
    return strings[ARG_REPOSITORY]!! to strings[ARG_NUMBER]
}

class PullRequestCommand : SubSlashCommand(), OnGuildChatInputCommandInteractionCreateEvent {
    override val name: String = "pull-request"
    override val description: String = "Get pull request from GitHub"
    override val config: SubSlashCommandConfig = {
        string(ARG_REPOSITORY, "GitHub repository") {
            required = true
            choice("Tachiyomi", "tachiyomi")
            choice("Tachiyomi Extensions", "tachiyomi-extensions")
            choice("Tachiyomi Website", "website")
            choice("Tachiyomi 1.x", "tachiyomi-1.x")
            choice("Tachiyomi Extensions 1.x", "tachiyomi-extensions-1.x")
        }
        string(ARG_NUMBER, "GitHub pull request ID") {
            required = true
        }
    }

    override fun onGuildChatInputCommandInteractionCreateEvent(): suspend GuildChatInputCommandInteractionCreateEvent.() -> Unit = {
        val (repository, number) = interaction.command.getArguments()

        val response = interaction.deferPublicResponse()

        try {
            val data = GitHubApi.getIssueOrPullRequest(OWNER, repository, number)
            val tags = URL("https://github.com/$OWNER/$repository/pull/$number").getOpenGraphTags()
            response.respond(data.buildResponse(repository, tags))
        } catch (e: Exception) {
            e.printStackTrace()
            response.respond {
                content = "Issue or Pull Request not found"
            }
            delay(1000)
            response.delete()
        }
    }
}
