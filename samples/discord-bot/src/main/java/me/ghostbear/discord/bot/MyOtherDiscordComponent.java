package me.ghostbear.discord.bot;

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import me.ghostbear.core.discord4j.annotations.DiscordInteractionHandler;
import me.ghostbear.core.discord4j.annotations.DiscordApplicationCommandProperties;
import me.ghostbear.core.discord4j.annotations.DiscordComponent;
import org.reactivestreams.Publisher;

@DiscordComponent
public class MyOtherDiscordComponent {

	@DiscordApplicationCommandProperties
	public String applicationCommandPath() {
		return "commands/test.json";
	}

	@DiscordInteractionHandler(name = "ping")
	public Publisher<?> myCommand(ChatInputInteractionEvent event) {
		return event.reply("Pong!");
	}

}
