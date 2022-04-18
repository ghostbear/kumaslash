package me.ghostbear.kumaslash.commands.base

import dev.kord.core.event.interaction.GuildMessageCommandInteractionCreateEvent

interface OnGuildMessageCommandInteractionCreateEvent {

    fun onGuildMessageCommandInteractionCreateEvent(): suspend GuildMessageCommandInteractionCreateEvent.() -> Unit
}