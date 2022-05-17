package me.ghostbear.kumaslash.commands.github

import com.haroldadmin.opengraphKt.getOpenGraphTags
import dev.kord.common.Color
import dev.kord.core.behavior.interaction.respondPublic
import dev.kord.core.event.interaction.GuildChatInputCommandInteractionCreateEvent
import dev.kord.rest.builder.component.ActionRowBuilder
import dev.kord.rest.builder.interaction.string
import dev.kord.rest.builder.message.create.embed
import me.ghostbear.core.OnGuildChatInputCommandInteractionCreateEvent
import me.ghostbear.core.SubSlashCommand
import me.ghostbear.core.SubSlashCommandConfig
import java.net.URL

class IssueCommand : SubSlashCommand(), OnGuildChatInputCommandInteractionCreateEvent {
    override val name: String = "issue"
    override val description: String = "Get issues from GitHub"
    override val config: SubSlashCommandConfig = {
        string("repository", "GitHub repository") {
            required = true
            choice("Tachiyomi", "tachiyomi")
            choice("Tachiyomi Extensions", "tachiyomi-extensions")
            choice("Tachiyomi Website", "website")
            choice("Tachiyomi 1.x", "tachiyomi-1.x")
            choice("Tachiyomi Extensions 1.x", "tachiyomi-extensions-1.x")
        }
        string("number", "GitHub issue ID") {
            required = true
        }
    }

    override fun onGuildChatInputCommandInteractionCreateEvent(): suspend GuildChatInputCommandInteractionCreateEvent.() -> Unit = {
        val command = interaction.command

        val user = "tachiyomiorg"
        val repository = command.strings["repository"]!!
        val id = command.strings["number"]!!

        val githubUrl = URL("https://github.com/$user/$repository/issues/$id").getOpenGraphTags()
        val githubTitle = githubUrl.title?.split(" · ")?.toTypedArray()

        var issueTitle = githubTitle?.get(0)?.substringBeforeLast(" by ")
        var issueRepository = githubTitle?.get(2)
        var issueNumber = githubTitle?.get(1)
        var issueType = if (issueNumber?.contains("Pull Request", ignoreCase = true) == true) {
            "Pull Request"
        } else if (issueNumber?.contains("Issue", ignoreCase = true) == true) {
            "Issue"
        } else {
            "Unknown"
        }

        try {
            interaction.respondPublic {
                embed {
                    color = Color(47, 49, 54)
                    image = githubUrl.image
                    url = githubUrl.url
                    title = issueTitle
                    description = githubUrl.description
                    author {
                        name = "$issueRepository · #${issueNumber?.substringAfterLast("#")}"
                    }
                    if (issueType == "Pull Request") {
                        footer {
                            text = "$issueType by ${githubTitle?.get(0)?.substringAfterLast(" by ")}"
                        }
                    }
                }
                components.add(
                    ActionRowBuilder().apply {
                        githubUrl.url?.let {
                            linkButton(it) {
                                label = "Open ${issueType.lowercase()} in browser"
                            }
                        }
                    }
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
