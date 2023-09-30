package me.ghostbear.kumaslash.core.commands;

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import me.ghostbear.core.discord4j.annotations.DiscordComponent;
import me.ghostbear.core.discord4j.annotations.DiscordInteractionHandler;
import me.ghostbear.core.discord4j.annotations.DiscordInteractionProperties;
import org.springframework.boot.info.BuildProperties;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

@DiscordComponent
public class VersionEventHandler {

	public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM);

	private final BuildProperties buildProperties;

	public VersionEventHandler(BuildProperties buildProperties) {
		this.buildProperties = buildProperties;
	}

	@DiscordInteractionProperties
	public String getName() {
		return "commands/version.json";
	}

	@DiscordInteractionHandler(name = "version")
	public Mono<Void> onCommandVersion(ChatInputInteractionEvent event) {
		return event.reply()
				.withEphemeral(true)
				.withContent(content());
	}

	String content() {
		return """
				**Version:** %s
				**Timestamp:** %s
				""".formatted(version(), time());
	}

	String version() {
		return buildProperties.getVersion();
	}

	String time() {
		LocalDateTime dateTime = LocalDateTime.ofInstant(buildProperties.getTime(), ZoneOffset.UTC);
		return DATE_TIME_FORMATTER.format(dateTime);
	}
}
