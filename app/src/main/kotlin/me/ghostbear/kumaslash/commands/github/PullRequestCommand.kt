package me.ghostbear.kumaslash.commands.github

import com.haroldadmin.opengraphKt.getOpenGraphTags
import dev.kord.common.Color
import dev.kord.core.behavior.interaction.response.respond
import dev.kord.core.entity.interaction.InteractionCommand
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
import me.ghostbear.data.github.model.Pull
import me.ghostbear.kumaslash.client

internal const val OWNER = "tachiyomiorg"
internal const val ARG_REPOSITORY = "repository"
internal const val ARG_NUMBER = "number"

fun InteractionCommand.getArguments(): Pair<String, String?> {
    return strings[ARG_REPOSITORY]!! to strings[ARG_NUMBER]
}

data class GitHubOpenGraph(
    val title: String?,
    val author: String?,
    val repository: String?,
    val number: String?,
    val type: String?,
    val image: String?,
    val url: String?,
    val description: String?
) {
    companion object {
        suspend fun create(url: URL): GitHubOpenGraph {
            val tags = url.getOpenGraphTags()
            val tagTitle = tags.title?.split(" · ")
                ?: throw Exception("Unable to extract Open Graph data")

            val title = tagTitle.getOrNull(0)?.split(" by ")
            val number = tagTitle.getOrNull(1)
            return GitHubOpenGraph(
                title?.getOrNull(0),
                title?.getOrNull(1),
                tagTitle.getOrNull(2),
                number,
                when {
                    number?.contains("Pull Request", ignoreCase = true) == true -> "Pull Request"
                    number?.contains("Issue", ignoreCase = true) == true -> "Issue"
                    else -> "Unknown"
                },
                tags.image,
                tags.url,
                tags.description
            )
        }
    }
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
            val data = client.get("https://api.github.com/repos/$OWNER/$repository/pulls/$number").body<Pull>()
            val tags = URL("https://github.com/$OWNER/$repository/pull/$number").getOpenGraphTags()

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
                    footer {
                        text = "Pull Request by ${data.user.login}"
                    }
                }
                components = mutableListOf(
                    ActionRowBuilder().apply {
                        linkButton(data.htmlUrl) {
                            label = "Open pull Request in browser"
                        }
                    }
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
            response.respond {
                content = "Pull Request not found use `/github issue` for Issues"
            }
            delay(1000)
            response.delete()
        }
    }
}
