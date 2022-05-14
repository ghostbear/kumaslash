package me.ghostbear.core

import dev.kord.core.event.interaction.GuildMessageCommandInteractionCreateEvent

interface OnGuildMessageCommandInteractionCreateEvent {

    fun onGuildMessageCommandInteractionCreateEvent(): suspend GuildMessageCommandInteractionCreateEvent.() -> Unit
}