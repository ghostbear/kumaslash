package me.ghostbear.kumaslash.commands.base

import dev.kord.core.event.interaction.GuildChatInputCommandInteractionCreateEvent

interface OnGuildChatInputCommandInteractionCreateEvent {

    fun onGuildChatInputCommandInteractionCreateEvent(): suspend GuildChatInputCommandInteractionCreateEvent.() -> Unit
}
