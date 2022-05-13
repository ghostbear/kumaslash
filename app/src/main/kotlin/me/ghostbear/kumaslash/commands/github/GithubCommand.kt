package me.ghostbear.kumaslash.commands.github

import me.ghostbear.core.SlashCommand
import me.ghostbear.core.SlashCommandGroup

class GithubCommand : SlashCommandGroup() {
    override val name: String = "github"
    override val description: String = "Github related commands"

    override val subcommands: List<SlashCommand> = listOf(
        IssueCommand(),
        PullRequestCommand(),
    )
}
