package me.ghostbear.core

import dev.kord.core.event.interaction.GuildChatInputCommandInteractionCreateEvent

interface OnGuildChatInputCommandInteractionCreateEvent {

    fun onGuildChatInputCommandInteractionCreateEvent(): suspend GuildChatInputCommandInteractionCreateEvent.() -> Unit
}
