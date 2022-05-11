package me.ghostbear.kumaslash.commands.github

import dev.kord.core.behavior.interaction.respondPublic
import dev.kord.core.event.interaction.GuildChatInputCommandInteractionCreateEvent
import dev.kord.rest.builder.component.ActionRowBuilder
import dev.kord.rest.builder.interaction.OptionsBuilder
import dev.kord.rest.builder.interaction.StringChoiceBuilder
import me.ghostbear.core.OnGuildChatInputCommandInteractionCreateEvent
import me.ghostbear.core.SlashCommand


class PullRequestCommand : SlashCommand(), OnGuildChatInputCommandInteractionCreateEvent {
    override val name: String = "pull-request"
    override val description: String = "Get pull request from GitHub"
    override val parameters: MutableList<OptionsBuilder> = mutableListOf(
        StringChoiceBuilder("repository", "GitHub repository").apply {
            required = true
            choice("Tachiyomi", "tachiyomi")
            choice("Tachiyomi Extensions", "tachiyomi-extensions")
        },
        StringChoiceBuilder("number", "GitHub pull request ID").apply {
            required = true
        }
    )

    override fun onGuildChatInputCommandInteractionCreateEvent(): suspend GuildChatInputCommandInteractionCreateEvent.() -> Unit = {
        val command = interaction.command

        val user = "tachiyomiorg"
        val repository = command.strings["repository"]!!
        val number = command.strings["number"]!!

        val titleRegex = Regex("(.*?)(?:\\sÂ·\\s)(.*?)(?:\\sÂ·\\s)(.*)(?:\\sÂ·\\s)(.*)")
        val githubUrl = "https://github.com/$user/$repository/pull/$number"
        val githubUrlTags = githubUrl.getOpenGraphTags()

        try {
            interaction.respondPublic {
                content = githubUrl
                components.add(
                    ActionRowBuilder().apply {
                        linkButton(githubUrl) {
                            label = "Open pull request in browser"
                        }
                    }
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
