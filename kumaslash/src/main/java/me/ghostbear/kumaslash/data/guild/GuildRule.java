package me.ghostbear.kumaslash.data.guild;

import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.util.UUID;

@Table("guild_rule")
public record GuildRule(
		@Column("uuid")
		UUID uuid,
		@Column("fk_guild_snowflake")
		long guildSnowflake,
		@Column("index")
		int index,
		@Column("title")
		String title,
		@Column("description")
		String description
) {
}
