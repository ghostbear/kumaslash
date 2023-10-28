package me.ghostbear.discord.bot;

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import me.ghostbear.core.discord4j.utils.Resources;
import me.ghostbear.core.discord4j.annotations.DiscordComponent;
import me.ghostbear.core.discord4j.annotations.DiscordInteractionHandler;
import me.ghostbear.core.discord4j.annotations.DiscordInteractionProperties;
import org.reactivestreams.Publisher;

@DiscordComponent
public class MyOtherDiscordComponent {

	@DiscordInteractionProperties
	public Resources commandProperties() {
		return Resources.of("commands/test.json");
	}

	@DiscordInteractionHandler(name = "ping")
	public Publisher<?> myCommand(ChatInputInteractionEvent event) {
		return event.reply("Pong!");
	}

}
