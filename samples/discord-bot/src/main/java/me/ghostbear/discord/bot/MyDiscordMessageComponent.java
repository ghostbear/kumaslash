package me.ghostbear.discord.bot;

import discord4j.core.event.domain.interaction.MessageInteractionEvent;
import me.ghostbear.core.discord4j.annotations.DiscordInteractionHandler;
import me.ghostbear.core.discord4j.annotations.DiscordApplicationCommandProperties;
import me.ghostbear.core.discord4j.annotations.DiscordComponent;
import reactor.core.publisher.Mono;

@DiscordComponent
public class MyDiscordMessageComponent {

	@DiscordApplicationCommandProperties
	public String properties() {
		// language=json
		return """
				{
				  "name": "Pin Toggle",
				  "type": 3
				}
				""";
	}

	@DiscordInteractionHandler(name = "Pin Toggle")
	public Mono<?> bop(MessageInteractionEvent event) {
		return event.deferReply()
				.withEphemeral(true)
				.then(event.getTargetMessage())
				.flatMap(message -> (message.isPinned() ? message.unpin() : message.pin()).thenReturn(message))
				.flatMap(message -> Mono.defer(() -> event.createFollowup("Pinned message %s".formatted(message.getId().asString()))));
	}

}
