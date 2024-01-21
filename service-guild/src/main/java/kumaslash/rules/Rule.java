/*
 * Copyright (C) 2023 ghostbear
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package kumaslash.rules;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceCreator;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.util.UUID;

@Table(name = "rules")
public record Rule(
		@Id UUID id,
		@Column("guild_snowflake") Long guildSnowflake,
		Double number,
		@Column("short") String shortDescription,
		@Column("long") String longDescription,
		@Transient boolean isNew)
		implements Persistable<UUID> {

	@PersistenceCreator
	public Rule(
			UUID id,
			Long guildSnowflake,
			Double number,
			String shortDescription,
			String longDescription) {
		this(id, guildSnowflake, number, shortDescription, longDescription, false);
	}

	@Override
	public UUID getId() {
		return id;
	}

	@Override
	public boolean isNew() {
		return isNew;
	}
}
