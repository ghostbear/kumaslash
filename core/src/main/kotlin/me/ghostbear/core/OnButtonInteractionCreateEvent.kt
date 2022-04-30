package me.ghostbear.core

import dev.kord.core.event.interaction.ButtonInteractionCreateEvent

interface OnButtonInteractionCreateEvent {

    fun onButtonInteractionCreateEvent(): suspend ButtonInteractionCreateEvent.() -> Unit
}
