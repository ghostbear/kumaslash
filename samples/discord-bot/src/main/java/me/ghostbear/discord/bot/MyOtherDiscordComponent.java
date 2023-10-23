package me.ghostbear.discord.bot;

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import me.ghostbear.core.discord4j.DiscordInteractionPropertySupplier;
import me.ghostbear.core.discord4j.annotations.DiscordComponent;
import me.ghostbear.core.discord4j.annotations.DiscordInteractionHandler;
import me.ghostbear.core.discord4j.annotations.DiscordInteractionProperties;
import org.reactivestreams.Publisher;

@DiscordComponent
public class MyOtherDiscordComponent {

	@DiscordInteractionProperties
	public DiscordInteractionPropertySupplier commandProperties() {
		return () -> "commands/test.json";
	}

	@DiscordInteractionHandler(name = "ping")
	public Publisher<?> myCommand(ChatInputInteractionEvent event) {
		return event.reply("Pong!");
	}

}
