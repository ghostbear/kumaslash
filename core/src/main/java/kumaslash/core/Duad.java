/*
 * Copyright (C) 2023 ghostbear
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package kumaslash.core;

import java.util.Objects;
import java.util.function.Function;

public record Duad<T, U>(T left, U right) {

	public static <T> Duad<T, T> of(T... args) {
		assert args.length == 2;
		return new Duad<>(args[0], args[1]);
	}

	public <U> U left(Function<T, U> mapper) {
		Objects.requireNonNull(mapper);
		return mapper.apply(left);
	}

	public <U1> U1 right(Function<U, U1> mapper) {
		Objects.requireNonNull(mapper);
		return mapper.apply(right);
	}
}
