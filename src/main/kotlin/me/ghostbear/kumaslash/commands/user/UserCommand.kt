package me.ghostbear.kumaslash.commands.user

import me.ghostbear.kumaslash.commands.base.SlashCommand
import me.ghostbear.kumaslash.commands.base.SlashCommandGroup

class UserCommand : SlashCommandGroup() {
    override val name: String = "user"
    override val description: String = "User related commands"

    override val subcommands: List<SlashCommand> = listOf(
        AvatarCommand(),
        BannerCommand()
    )
}
