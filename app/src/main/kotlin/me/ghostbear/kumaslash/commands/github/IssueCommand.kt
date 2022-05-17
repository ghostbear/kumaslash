package me.ghostbear.kumaslash.commands.github

import dev.kord.common.Color
import dev.kord.core.behavior.interaction.respondEphemeral
import dev.kord.core.behavior.interaction.respondPublic
import dev.kord.core.event.interaction.GuildChatInputCommandInteractionCreateEvent
import dev.kord.rest.builder.component.ActionRowBuilder
import dev.kord.rest.builder.interaction.string
import dev.kord.rest.builder.message.create.embed
import java.net.URL
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

        try {
            val githubUrl = URL("https://github.com/$USER/$repository/issues/$number")
            val gitHubOpenGraph = GitHubOpenGraph.create(githubUrl)

            interaction.respondPublic {
                embed {
                    color = Color(47, 49, 54)
                    image = gitHubOpenGraph.image
                    url = gitHubOpenGraph.url
                    title = gitHubOpenGraph.title
                    description = gitHubOpenGraph.description
                    author {
                        name = "${gitHubOpenGraph.repository} · #${gitHubOpenGraph.number?.substringAfterLast("#")}"
                    }
                    if (gitHubOpenGraph.type == "Pull Request") {
                        footer {
                            text = "${gitHubOpenGraph.type} by ${gitHubOpenGraph.author}"
                        }
                    }
                }
                gitHubOpenGraph.url?.let {
                    components.add(
                        ActionRowBuilder().apply {
                            linkButton(it) {
                                label = "Open ${gitHubOpenGraph.type?.lowercase()} in browser"
                            }
                        }
                    )
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            interaction.respondEphemeral {
                content = "Something went wrong"
            }
        }
    }
}
