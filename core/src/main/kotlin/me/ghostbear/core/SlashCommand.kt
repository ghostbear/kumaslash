package me.ghostbear.core

import dev.kord.core.Kord
import dev.kord.rest.builder.interaction.ChatInputCreateBuilder
import dev.kord.rest.builder.interaction.OptionsBuilder
import dev.kord.rest.builder.interaction.SubCommandBuilder
import dev.kord.rest.builder.interaction.subCommand

typealias SlashCommandConfig = ChatInputCreateBuilder.() -> Unit
typealias SubSlashCommandConfig = SubCommandBuilder.() -> Unit

abstract class SlashCommand : Command {

    open val config: SlashCommandConfig = {}

    override fun register(): suspend Kord.() -> Unit = {
        createGlobalChatInputCommand(name, description, config)
    }
}

abstract class SubSlashCommand : Command {

    open val config: SubSlashCommandConfig = {}

    override fun register(): suspend Kord.() -> Unit = {
        throw Exception("This should be handled by the SlashCommandGroup")
    }
}

abstract class SlashCommandGroup : Command {

    open val subcommands: List<SubSlashCommand> = listOf()

    override fun register(): suspend Kord.() -> Unit = {
        createGlobalChatInputCommand(name, description) {
            subcommands.forEach { subcommand ->
                subCommand(subcommand.name, subcommand.description) {
                    subcommand.config(this)
                }
            }
        }
    }
}