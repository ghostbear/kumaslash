package me.ghostbear.kumaslash.commands.user

import me.ghostbear.core.SlashCommand
import me.ghostbear.core.SlashCommandGroup

class UserCommand : SlashCommandGroup() {
    override val name: String = "user"
    override val description: String = "User related commands"

    override val subcommands: List<SlashCommand> = listOf(
        AvatarCommand(),
        BannerCommand()
    )
}
