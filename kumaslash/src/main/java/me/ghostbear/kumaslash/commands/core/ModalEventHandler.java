package me.ghostbear.kumaslash.commands.core;

import discord4j.core.event.domain.interaction.ModalSubmitInteractionEvent;
import reactor.core.publisher.Mono;

public non-sealed interface ModalEventHandler extends EventHandler {

	String getCustomId();

	Mono<Void> handle(ModalSubmitInteractionEvent event);

}
