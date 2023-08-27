package me.ghostbear.kumaslash.commands;

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import me.ghostbear.core.discord4j.annotations.DiscordInteractionProperties;
import me.ghostbear.core.discord4j.annotations.DiscordComponent;
import me.ghostbear.core.discord4j.annotations.DiscordInteractionHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.info.BuildProperties;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

@DiscordComponent
public class VersionEventHandler {

	private final BuildProperties buildProperties;

	@Autowired
	public VersionEventHandler(BuildProperties buildProperties) {
		this.buildProperties = buildProperties;
	}

	@DiscordInteractionProperties()
	public String getName() {
		return "commands/version.json";
	}

	@DiscordInteractionHandler(name = "version")
	public Mono<Void> onCommandVersion(ChatInputInteractionEvent event) {
		DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM);
		ZonedDateTime localDateTime = LocalDateTime.ofInstant(buildProperties.getTime(), ZoneOffset.UTC).atZone(ZoneOffset.UTC);
		String formatted = dateTimeFormatter.format(localDateTime);
		return event.reply()
				.withEphemeral(true)
				.withContent("""
					**Version:** %s
					**Timestamp:** %s
					""".strip().formatted(buildProperties.getVersion(), formatted));
	}
}
