package me.ghostbear.kumaslash.commands.base

import dev.kord.core.event.interaction.ButtonInteractionCreateEvent

interface OnButtonInteractionCreateEvent {

    fun onButtonInteractionCreateEvent(): suspend ButtonInteractionCreateEvent.() -> Unit
}
