package me.ghostbear.kumaslash.guild.model;

import discord4j.common.util.Snowflake;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("guild_log_channel")
public record GuildLogChannel(
		@Column("channel_snowflake")
		Long channelSnowflake,
		@Column("fk_guild_snowflake")
		Long guildSnowflake,
		@Column("type")
		Type type
) {

	public GuildLogChannel(Snowflake channelSnowflake, Snowflake guildSnowflake, Type type) {
		this(channelSnowflake.asLong(), guildSnowflake.asLong(), type);
	}

	public enum Type {
		TIMEOUT
	}

}
