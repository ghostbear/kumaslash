package me.ghostbear.kumaslash.commands.github

import com.haroldadmin.opengraphKt.getOpenGraphTags
import dev.kord.common.Color
import dev.kord.core.behavior.interaction.response.respond
import dev.kord.core.event.interaction.GuildChatInputCommandInteractionCreateEvent
import dev.kord.rest.builder.component.ActionRowBuilder
import dev.kord.rest.builder.interaction.string
import dev.kord.rest.builder.message.modify.embed
import io.ktor.client.call.body
import io.ktor.client.request.get
import java.net.URL
import kotlinx.coroutines.delay
import me.ghostbear.core.OnGuildChatInputCommandInteractionCreateEvent
import me.ghostbear.core.SubSlashCommand
import me.ghostbear.core.SubSlashCommandConfig
import me.ghostbear.data.github.model.Issue
import me.ghostbear.kumaslash.client

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
            val data = client.get("https://api.github.com/repos/$OWNER/$repository/issues/$number").body<Issue>()
            val tags = URL("https://github.com/$OWNER/$repository/issues/$number").getOpenGraphTags()
            val isPullRequest = data.pullRequest != null

            response.respond {
                embed {
                    color = Color(47, 49, 54)
                    image = tags.image
                    url = data.htmlUrl
                    title = data.title
                    description = data.body
                    author {
                        name = "$repository · #${data.number}"
                    }
                    if (isPullRequest) {
                        footer {
                            icon = data.user.avatarUrl
                            text = "Pull request by ${data.user.login}"
                        }
                    }
                }
                components = mutableListOf(
                    ActionRowBuilder().apply {
                        linkButton(data.htmlUrl) {
                            label = "Open ${if (isPullRequest) "pull request" else "issue"} in browser"
                        }
                    }
                )
            }
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
