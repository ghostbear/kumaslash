package me.ghostbear.kumaslash.commands.user

import me.ghostbear.core.SlashCommandGroup
import me.ghostbear.core.SubSlashCommand

class UserCommand : SlashCommandGroup() {
    override val name: String = "user"
    override val description: String = "User related commands"

    override val subcommands: List<SubSlashCommand> = listOf(
        AvatarCommand(),
        BannerCommand()
    )
}
