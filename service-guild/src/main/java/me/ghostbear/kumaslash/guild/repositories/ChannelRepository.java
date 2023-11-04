package me.ghostbear.kumaslash.guild.repositories;

import me.ghostbear.kumaslash.guild.domain.Channel;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.repository.query.Param;
import reactor.core.publisher.Mono;

public interface ChannelRepository extends R2dbcRepository<Channel, Long> {

	@Query("""
			INSERT INTO guild_log_channel(channel_snowflake, fk_guild_snowflake, type)
			VALUES (:#{#channel.channelSnowflake}, :#{#channel.guildSnowflake}, :#{#channel.type}::log_type)
			ON CONFLICT
			DO NOTHING;
			""")
	Mono<Void> insert(@Param("channel") Channel channel);

	Mono<Channel> findByGuildSnowflake(long guildSnowflake);
}
