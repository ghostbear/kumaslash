/*
 * Copyright (C) 2023 ghostbear
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package kumaslash.socials;

import java.util.IllegalFormatException;
import java.util.Objects;
import java.util.function.Predicate;

public class SocialTemplateValidator implements Predicate<String> {

	private final Object[] args;

	/**
	 * @param args the values to be used with the template.
	 * @throws NullPointerException when args is null.
	 * @throws IllegalStateException when args length is less than or greater than 2.
	 */
	public SocialTemplateValidator(Object... args) {
		Objects.requireNonNull(args);
		if (args.length != 2) {
			throw new IllegalStateException("args required length is 2");
		}
		this.args = args;
	}

	@Override
	public boolean test(String s) {
		try {
			if (s == null) return false;
			String ignored = s.formatted(args);
			return s.contains("%s");
		} catch (IllegalFormatException e) {
			return false;
		}
	}
}
