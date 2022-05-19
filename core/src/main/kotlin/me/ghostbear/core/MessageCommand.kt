package me.ghostbear.core

import dev.kord.common.DiscordBitSet
import dev.kord.common.entity.Permissions
import dev.kord.core.Kord

abstract class MessageCommand : Command {

    override val description: String = ""

    override fun register(): suspend Kord.() -> Unit = {
        createGlobalMessageCommand(name) {
            defaultMemberPermissions = Permissions(DiscordBitSet(0))
        }
    }
}
