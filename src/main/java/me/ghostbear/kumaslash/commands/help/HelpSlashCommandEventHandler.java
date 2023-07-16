package me.ghostbear.kumaslash.commands.help;

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.component.ActionRow;

import java.util.Arrays;
import java.util.stream.Collectors;

import me.ghostbear.kumaslash.commands.core.SlashCommandEventHandler;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class HelpSlashCommandEventHandler implements SlashCommandEventHandler.SlashCommand {

	@Override
	public String getName() {
		return "help";
	}

	@Override
	public Mono<Void> handle(ChatInputInteractionEvent event) {
		var actionComponents = new HelpActionComponentFactory().create();
		return event.presentModal()
				.withCustomId(HelpModalEventHandler.CUSTOM_ID)
				.withTitle("Answer the following questions")
				.withComponents(
						Arrays.stream(actionComponents)
								.map(ActionRow::of)
								.collect(Collectors.toList()));
	}
}
