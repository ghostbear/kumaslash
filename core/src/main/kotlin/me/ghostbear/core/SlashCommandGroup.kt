package me.ghostbear.core

import dev.kord.core.Kord
import dev.kord.rest.builder.interaction.subCommand

abstract class SlashCommandGroup : Command {

    open val subcommands: List<SlashCommand> = listOf()

    override fun register(): suspend Kord.() -> Unit = {
        createGlobalChatInputCommand(name, description) {
            subcommands.forEach { subcommand ->
                subCommand(subcommand.name, subcommand.description) {
                    options = subcommand.parameters
                }
            }
        }
    }
}
