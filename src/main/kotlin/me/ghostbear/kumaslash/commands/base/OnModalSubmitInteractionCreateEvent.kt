package me.ghostbear.kumaslash.commands.base

import dev.kord.core.event.interaction.ModalSubmitInteractionCreateEvent

interface OnModalSubmitInteractionCreateEvent {

    fun onModalSubmitInteractionCreateEvent(): suspend ModalSubmitInteractionCreateEvent.() -> Unit
}
