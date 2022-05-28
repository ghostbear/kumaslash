package me.ghostbear.core

import dev.kord.common.DiscordBitSet
import dev.kord.common.entity.Permissions
import dev.kord.core.Kord
import dev.kord.rest.builder.interaction.GlobalMessageCommandCreateBuilder

typealias MessageCommandConfig = GlobalMessageCommandCreateBuilder.() -> Unit

abstract class MessageCommand : Command {

    override val description: String = ""

    open val config: MessageCommandConfig = {}

    override fun register(): suspend Kord.() -> Unit = {
        createGlobalMessageCommand(name) {
            apply(config)
        }
    }
}
