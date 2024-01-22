/*
 * Copyright (C) 2023 ghostbear
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package kumaslash.anilist.model;

import java.time.LocalDate;
import java.util.Objects;

public record FuzzyDate(Integer year, Integer month, Integer day) {

	public LocalDate asLocalDate() {
		return LocalDate.of(
				Objects.requireNonNullElse(year, 1970),
				Objects.requireNonNullElse(month, 1),
				Objects.requireNonNullElse(day, 1));
	}
}
