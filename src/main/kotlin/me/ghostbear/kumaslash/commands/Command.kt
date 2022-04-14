package me.ghostbear.kumaslash.commands

import dev.kord.core.Kord

interface Command {

    val name: String
    val description: String

    fun register(): suspend Kord.() -> Unit
}
