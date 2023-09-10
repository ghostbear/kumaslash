package me.ghostbear.kumaslash.guild.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.util.UUID;

@Table("guild")
public record GuildSocial(
		@Id
		@Column("uuid")
		UUID id,
		@Column("fk_guild_snowflake")
		long guildSnowflake,
		@Column("image_url")
		String imageUrl,
		@Column("action")
		Action action
) {



	public enum Action {
		BITE,
		HUG,
		KISS,
		LICK,
		NUZZLE,
		SLAP,
		HEADPAT
	}

}
