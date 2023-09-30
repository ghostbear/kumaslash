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
		return findGuildById(snowflake).switchIfEmpty(Mono.defer(() -> createNewGuild(snowflake)))
				.doOnSuccess(guild -> LOG.debug("Registered guild with snowflake {}", snowflake.asString()));
	}

	Mono<Guild> findGuildById(Snowflake snowflake) {
		return guildRepository.findById(snowflake.asLong())
				.doOnError(throwable -> LOG.error("Failed to find guild", throwable));
	}

	Mono<Guild> createNewGuild(Snowflake snowflake) {
		return guildRepository.save(new Guild(snowflake, true))
				.doOnError(throwable -> LOG.error("Failed to save guild", throwable));
	}

}
