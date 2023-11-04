package me.ghostbear.kumaslash.guild.domain;

import discord4j.common.util.Snowflake;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("guild_log_channel")
public record Channel(
		@Column("channel_snowflake")
		Long channelSnowflake,
		@Column("fk_guild_snowflake")
		Long guildSnowflake,
		@Column("type")
		Type type
) {

	public Channel(Snowflake channelSnowflake, Snowflake guildSnowflake, Type type) {
		this(channelSnowflake.asLong(), guildSnowflake.asLong(), type);
	}

	public enum Type {
		TIMEOUT
	}

}
