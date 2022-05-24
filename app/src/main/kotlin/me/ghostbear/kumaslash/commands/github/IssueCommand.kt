package me.ghostbear.kumaslash.commands.github

import com.haroldadmin.opengraphKt.getOpenGraphTags
import dev.kord.core.behavior.interaction.response.respond
import dev.kord.core.event.interaction.GuildChatInputCommandInteractionCreateEvent
import dev.kord.rest.builder.interaction.string
import java.net.URL
import kotlinx.coroutines.delay
import me.ghostbear.core.OnGuildChatInputCommandInteractionCreateEvent
import me.ghostbear.core.SubSlashCommand
import me.ghostbear.core.SubSlashCommandConfig

class IssueCommand : SubSlashCommand(), OnGuildChatInputCommandInteractionCreateEvent {
    override val name: String = "issue"
    override val description: String = "Get issues from GitHub"
    override val config: SubSlashCommandConfig = {
        string(ARG_REPOSITORY, "GitHub repository") {
            required = true
            choice("Tachiyomi", "tachiyomi")
            choice("Tachiyomi Extensions", "tachiyomi-extensions")
            choice("Tachiyomi Website", "website")
            choice("Tachiyomi 1.x", "tachiyomi-1.x")
            choice("Tachiyomi Extensions 1.x", "tachiyomi-extensions-1.x")
        }
        string(ARG_NUMBER, "GitHub issue ID") {
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
