/*
 * Copyright (C) 2023 ghostbear
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package kumaslash.socials;

import java.util.UUID;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceCreator;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("socials_action")
public record SocialAction(
		@Id UUID id,
		@Column("guild_snowflake") Long guildSnowflake,
		@Column("action") String action,
		@Column("template") String template,
		@Transient boolean isNew)
		implements Persistable<UUID> {

	@PersistenceCreator
	public SocialAction(UUID id, Long guildSnowflake, String action, String template) {
		this(id, guildSnowflake, action, template, false);
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
