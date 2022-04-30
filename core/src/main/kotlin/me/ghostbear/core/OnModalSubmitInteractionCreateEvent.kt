package me.ghostbear.core

import dev.kord.core.event.interaction.ModalSubmitInteractionCreateEvent

interface OnModalSubmitInteractionCreateEvent {

    fun onModalSubmitInteractionCreateEvent(): suspend ModalSubmitInteractionCreateEvent.() -> Unit
}
