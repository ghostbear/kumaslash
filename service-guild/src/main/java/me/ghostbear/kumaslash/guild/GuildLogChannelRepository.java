package me.ghostbear.kumaslash.guild;

import me.ghostbear.kumaslash.guild.model.GuildLogChannel;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.repository.query.Param;
import reactor.core.publisher.Mono;

public interface GuildLogChannelRepository extends R2dbcRepository<GuildLogChannel, Long> {

	@Query("""
			INSERT INTO guild_log_channel(channel_snowflake, fk_guild_snowflake, type)
			VALUES (:#{#channel.channelSnowflake}, :#{#channel.guildSnowflake}, :#{#channel.type}::log_type)
			ON CONFLICT
			DO NOTHING;
			""")
	Mono<Void> insert(@Param("channel") GuildLogChannel channel);

    Mono<GuildLogChannel> findByGuildSnowflake(long guildSnowflake);
}
