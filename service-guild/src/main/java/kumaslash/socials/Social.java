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

@Table(name = "socials")
public record Social(
		@Id UUID id,
		@Column("socials_action_id") UUID socialsActionId,
		@Column("url") String url,
		@Transient boolean isNew)
		implements Persistable<UUID> {

	@PersistenceCreator
	public Social(UUID id, UUID socialsActionId, String url) {
		this(id, socialsActionId, url, false);
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
