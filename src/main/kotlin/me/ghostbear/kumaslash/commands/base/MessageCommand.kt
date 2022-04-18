package me.ghostbear.kumaslash.commands.base

import dev.kord.core.Kord

abstract class MessageCommand : Command {

    override val description: String = ""

    override fun register(): suspend Kord.() -> Unit = {
        createGlobalMessageCommand(name)
    }
}