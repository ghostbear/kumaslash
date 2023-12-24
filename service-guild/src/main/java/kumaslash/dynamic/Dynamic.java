/*
 * Copyright (C) 2023 ghostbear
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package kumaslash.dynamic;

import org.springframework.data.annotation.Id;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Table;

@Table(name = "shout")
public record Dynamic(@Id Long id, Long snowflake, String value) implements Persistable<Long> {

	@Override
	public Long getId() {
		return id;
	}

	@Override
	public boolean isNew() {
		return true;
	}
}
