package me.ghostbear.kumaslash.guild.domain;

import discord4j.common.util.Snowflake;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceCreator;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("guild")
public record Guild(
		@Id
		@Column("snowflake")
		long snowflake,
		@Transient
		boolean isNew) implements Persistable<Long> {

	@PersistenceCreator
	public Guild(long snowflake) {
		this(snowflake, false);
	}

	public Guild(Snowflake snowflake, boolean isNew) {
		this(snowflake.asLong(), isNew);
	}

	public Snowflake asSnowflake() {
		return Snowflake.of(snowflake);
	}

	@Override
	public Long getId() {
		return snowflake;
	}

}
