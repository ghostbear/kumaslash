package me.ghostbear.core

import dev.kord.core.Kord
import dev.kord.rest.builder.interaction.OptionsBuilder

abstract class SlashCommand : Command {

    open val parameters: MutableList<OptionsBuilder> = mutableListOf()

    override fun register(): suspend Kord.() -> Unit = {
        createGlobalChatInputCommand(name, description) {
            options = parameters
        }
    }
}
