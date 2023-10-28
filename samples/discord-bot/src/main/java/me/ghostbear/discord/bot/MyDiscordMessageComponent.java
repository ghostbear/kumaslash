package me.ghostbear.discord.bot;

import discord4j.core.event.domain.interaction.MessageInteractionEvent;
import me.ghostbear.core.discord4j.utils.Raw;
import me.ghostbear.core.discord4j.annotations.DiscordComponent;
import me.ghostbear.core.discord4j.annotations.DiscordInteractionHandler;
import me.ghostbear.core.discord4j.annotations.DiscordInteractionProperties;
import reactor.core.publisher.Mono;

@DiscordComponent
public class MyDiscordMessageComponent {

	@DiscordInteractionProperties
	public Raw commandProperties() {
		// language=json
		return () -> """
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
