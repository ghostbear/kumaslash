package me.ghostbear.core

import dev.kord.core.Kord

interface Register {

    val name: String
    val description: String

    fun register(): suspend Kord.() -> Unit
}
