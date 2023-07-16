package me.ghostbear.kumaslash.commands.core;

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.command.ApplicationCommandInteractionOption;
import reactor.core.publisher.Mono;

public sealed interface SlashCommandEventHandler extends EventHandler {

	String getName();

	non-sealed interface SlashCommand extends SlashCommandEventHandler {
		Mono<Void> handle(ChatInputInteractionEvent event);
	}

	non-sealed interface SubSlashCommand extends SlashCommandEventHandler {

		String getSubName();

		Mono<Void> handle(ChatInputInteractionEvent event, ApplicationCommandInteractionOption option);
	}

}
