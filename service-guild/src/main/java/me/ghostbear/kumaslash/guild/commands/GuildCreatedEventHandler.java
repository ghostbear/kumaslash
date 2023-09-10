package me.ghostbear.kumaslash.guild.commands;

import discord4j.common.util.Snowflake;
import discord4j.core.event.domain.guild.GuildCreateEvent;
import me.ghostbear.core.discord4j.annotations.DiscordComponent;
import me.ghostbear.core.discord4j.annotations.DiscordEventHandler;
import me.ghostbear.kumaslash.guild.GuildRepository;
import me.ghostbear.kumaslash.guild.model.Guild;
import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Mono;

import java.time.Duration;

@DiscordComponent
public class GuildCreatedEventHandler {

	private static final Logger LOG = LoggerFactory.getLogger(GuildCreatedEventHandler.class);

	private final GuildRepository guildRepository;

	@Autowired
	public GuildCreatedEventHandler(GuildRepository guildRepository) {
		this.guildRepository = guildRepository;
	}

	@DiscordEventHandler
	public Publisher<?> onGuildCreate(GuildCreateEvent event) {
		Snowflake snowflake = event.getGuild().getId();
		LOG.info("Guild create event: {}", snowflake.asString());
		return guildRepository.findById(snowflake.asLong())
				.delayElement(Duration.ofSeconds(0))
				.switchIfEmpty(Mono.defer(() -> guildRepository.save(new Guild(snowflake, true))))
				.doOnError(throwable -> LOG.error("Failed to create new guild", throwable))
				.then();
	}

}
