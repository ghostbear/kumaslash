/*
 * Copyright (C) 2023 ghostbear
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package kumaslash.guild;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceCreator;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Table;

@Table("guild")
public record Guild(@Id Long id, @Transient boolean isNew) implements Persistable<Long> {

	@PersistenceCreator
	public Guild(Long id) {
		this(id, false);
	}

	@Override
	public Long getId() {
		return id;
	}

	@Override
	public boolean isNew() {
		return isNew;
	}
}
