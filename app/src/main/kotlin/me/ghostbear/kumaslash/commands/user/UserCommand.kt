package me.ghostbear.kumaslash.commands.user

import dev.kord.core.Kord
import dev.kord.rest.builder.interaction.subCommand
import me.ghostbear.kumaslash.util.SubCommandInitializer
import me.ghostbear.kumaslash.util.createChatInputCommand

private const val name: String = "user"
private const val description: String = "User related commands"

suspend fun Kord.userCommandGroup() {
    createChatInputCommand(name, description) {
        val init: SubCommandInitializer = { name, description, builder ->
            subCommand(name, description, builder)
        }
        avatarCommand(init)
        bannerCommand(init)
    }
}
