/*
 * Copyright (C) 2023 ghostbear
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package kumaslash.utils;

import java.util.Objects;
import java.util.function.Function;

public record Duad<R, L>(R right, L left) {

	public <U> U right(Function<R, U> mapper) {
		Objects.requireNonNull(mapper);
		return mapper.apply(right);
	}

	public <U> U left(Function<L, U> mapper) {
		Objects.requireNonNull(mapper);
		return mapper.apply(left);
	}
}
