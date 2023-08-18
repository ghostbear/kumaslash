package me.ghostbear.kumaslash.listeners;

import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.guild.GuildCreateEvent;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.event.domain.interaction.ModalSubmitInteractionEvent;
import discord4j.core.object.command.ApplicationCommandOption;
import me.ghostbear.kumaslash.commands.core.EventHandler;
import me.ghostbear.kumaslash.commands.core.GuildCreateEventHandler;
import me.ghostbear.kumaslash.commands.core.ModalEventHandler;
import me.ghostbear.kumaslash.commands.core.SlashCommandEventHandler;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.List;

@Component
public class EventHandlerListener {

	private final Collection<EventHandler> eventHandlers;

	public EventHandlerListener(GatewayDiscordClient client, List<EventHandler> eventHandlers) {
		this.eventHandlers = eventHandlers;
		client.on(GuildCreateEvent.class, this::handle).subscribe();
		client.on(ChatInputInteractionEvent.class, this::handle).subscribe();
		client.on(ModalSubmitInteractionEvent.class, this::handle).subscribe();
	}

	public Mono<Void> handle(ModalSubmitInteractionEvent event) {
		return Flux.fromIterable(eventHandlers)
				.filter(eventHandler -> eventHandler instanceof ModalEventHandler)
				.map(eventHandler -> (ModalEventHandler) eventHandler)
				.filter(eventHandler -> eventHandler.getCustomId().equals(event.getCustomId()))
				.next()
				.flatMap(eventHandler -> eventHandler.handle(event));
	}

	public Mono<Void> handle(ChatInputInteractionEvent event) {
		return Flux.fromIterable(eventHandlers)
				.filter(eventHandler -> eventHandler instanceof SlashCommandEventHandler)
				.map(eventHandler -> (SlashCommandEventHandler) eventHandler)
				.filter(eventHandler -> event.getOptions()
						.stream()
						.filter(option -> option.getType().equals(ApplicationCommandOption.Type.SUB_COMMAND))
						.findFirst()
						.map(option -> eventHandler instanceof SlashCommandEventHandler.SubSlashCommand subSlashCommand
								&& subSlashCommand.getName().equals(event.getCommandName())
								&& subSlashCommand.getSubName().equals(option.getName()))
						.orElseGet(() -> eventHandler.getName().equals(event.getCommandName())))
				.next()
				.flatMap(eventHandler -> {
					if (eventHandler instanceof SlashCommandEventHandler.SubSlashCommand subSlashCommand) {
						return subSlashCommand.handle(event, event.getOptions().get(0));
					} else if (eventHandler instanceof SlashCommandEventHandler.SlashCommand slashCommand) {
						return slashCommand.handle(event);
					} else {
						return Mono.empty();
					}
				});
	}

	public Flux<Void> handle(GuildCreateEvent event) {
		return Flux.fromIterable(eventHandlers)
				.filter(eventHandler -> eventHandler instanceof GuildCreateEventHandler)
				.map(eventHandler -> (GuildCreateEventHandler) eventHandler)
				.flatMap(eventHandlers -> eventHandlers.handle(event));
	}
}
