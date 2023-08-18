package me.ghostbear.kumaslash.commands.help;

import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.ReactiveEventAdapter;
import discord4j.core.event.domain.interaction.ButtonInteractionEvent;
import discord4j.core.event.domain.interaction.MessageInteractionEvent;
import discord4j.core.object.command.Interaction;
import discord4j.core.object.component.ActionRow;
import discord4j.core.object.component.Button;
import discord4j.core.object.entity.User;
import org.reactivestreams.Publisher;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.stream.Collectors;

@Component
public class HelpMessageInteractionEventHandler extends ReactiveEventAdapter {

	public HelpMessageInteractionEventHandler(GatewayDiscordClient gatewayDiscordClient) {
		gatewayDiscordClient.on(this).subscribe();
	}

	@Override
	public Publisher<?> onMessageInteraction(MessageInteractionEvent event) {
		if (!event.getInteraction().getType().equals(Interaction.Type.APPLICATION_COMMAND)) {
			return Mono.empty();
		}

		if (!event.getCommandName().equals("Help")) {
			return Mono.empty();
		}

		return event.deferReply()
				.then(event.createFollowup()
						.withContent("%s, please follow the Troubleshooting link below, if that doesn't solve your issues, please click the Help button.".formatted(event.getResolvedMessage().getAuthor().map(User::getMention).orElse("Unknown user")))
						.withComponents(ActionRow.of(
								Button.primary("help_button", "Help"),
								Button.link("https://tachiyomi.org/help/guides/troubleshooting/", "Troubleshooting")
						)));

	}

	@Override
	public Publisher<?> onButtonInteraction(ButtonInteractionEvent event) {
		if (!event.getCustomId().equals("help_button")) {
			return Mono.empty();
		}

		var actionComponents = new HelpActionComponentFactory().create();
		return event.presentModal()
				.withCustomId("help_slash_command")
				.withTitle("Answer the following questions")
				.withComponents(
						Arrays.stream(actionComponents)
								.map(ActionRow::of)
								.collect(Collectors.toList()));
	}
}
