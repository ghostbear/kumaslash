package me.ghostbear.kumaslash.data.guild;

import java.util.UUID;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

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
