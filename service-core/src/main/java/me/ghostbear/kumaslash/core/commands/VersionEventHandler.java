package me.ghostbear.kumaslash.core.commands;

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import me.ghostbear.core.discord4j.DiscordInteractionPropertySupplier;
import me.ghostbear.core.discord4j.annotations.DiscordComponent;
import me.ghostbear.core.discord4j.annotations.DiscordInteractionHandler;
import me.ghostbear.core.discord4j.annotations.DiscordInteractionProperties;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.info.BuildProperties;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Objects;

@DiscordComponent
public class VersionEventHandler {

	@NotNull
	public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM);

	@NotNull
	private final BuildProperties buildProperties;

	public VersionEventHandler(@NotNull BuildProperties buildProperties) {
		this.buildProperties = Objects.requireNonNull(buildProperties);
	}

	@DiscordInteractionProperties
	public DiscordInteractionPropertySupplier properties() {
		return () -> "commands/version.json";
	}

	@NotNull
	@DiscordInteractionHandler(name = "version")
	public Mono<Void> onCommandVersion(@NotNull ChatInputInteractionEvent event) {
		Objects.requireNonNull(event);
		return event.reply()
				.withEphemeral(true)
				.withContent(content());
	}

	@NotNull
	String content() {
		return """
				**Version:** %s
				**Timestamp:** %s
				""".formatted(version(), time());
	}

	@NotNull
	String version() {
		return buildProperties.getVersion();
	}

	@NotNull
	String time() {
		LocalDateTime dateTime = LocalDateTime.ofInstant(buildProperties.getTime(), ZoneOffset.UTC);
		return DATE_TIME_FORMATTER.format(dateTime);
	}
}
