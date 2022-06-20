package me.ghostbear.kumaslash.commands.github

import dev.kord.core.Kord
import dev.kord.rest.builder.interaction.subCommand
import me.ghostbear.kumaslash.util.createChatInputCommand

private const val NAME: String = "github"
private const val DESCRIPTION: String = "Github related commands"

suspend fun Kord.githubCommandGroup() {
    createChatInputCommand(NAME, DESCRIPTION) {
        issueCommand { name, description, builder ->
            subCommand(name, description, builder)
        }
    }
}
