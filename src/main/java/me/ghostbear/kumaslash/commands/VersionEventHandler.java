package me.ghostbear.kumaslash.commands;

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import me.ghostbear.kumaslash.commands.core.SlashCommandEventHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.info.BuildProperties;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class VersionEventHandler implements SlashCommandEventHandler.SlashCommand {

	private final BuildProperties buildProperties;

	@Autowired
	public VersionEventHandler(BuildProperties buildProperties) {
		this.buildProperties = buildProperties;
	}

	@Override
	public String getName() {
		return "version";
	}

	@Override
	public Mono<Void> handle(ChatInputInteractionEvent event) {
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
