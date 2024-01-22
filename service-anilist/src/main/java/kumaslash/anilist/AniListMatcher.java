/*
 * Copyright (C) 2023 ghostbear
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package kumaslash.anilist;

import org.springframework.stereotype.Component;
import org.springframework.util.function.SingletonSupplier;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.stream.Stream;

@Component
public class AniListMatcher {

	public Stream<Result> matches(String value) {
		return Arrays.stream(Pattern.values()).flatMap(p -> {
			java.util.regex.Pattern pattern = p.getPattern();
			Matcher matcher = pattern.matcher(value);
			return matcher.results().map(matchResult -> new Result(p, matchResult.group(1)));
		});
	}

	public boolean contains(String value) {
		return Arrays.stream(Pattern.values())
				.map(Pattern::getPattern)
				.map(pattern -> pattern.matcher(value))
				.anyMatch(Matcher::find);
	}

	public record Result(Pattern pattern, String match) {}

	public enum Pattern {
		ANIME('{', '}'),
		MANGA('<', '>'),
		LIGHT_NOVEL('[', ']');

		private final char left;
		private final char right;

		private final SingletonSupplier<java.util.regex.Pattern> patternSupplier =
				SingletonSupplier.of(() -> java.util.regex.Pattern.compile(getPatternRaw()));

		Pattern(char left, char right) {
			this.left = left;
			this.right = right;
		}

		public String getPatternRaw() {
			return "(?<![\\w\\%s])\\%s{2}(?!\\%s)(\\S(?:[^\\%s\\%s}]*\\S)?)(?<!\\%s)\\%s{2}(?![\\%s\\w])"
					.formatted(left, left, left, left, right, right, right, right);
		}

		public java.util.regex.Pattern getPattern() {
			return patternSupplier.obtain();
		}
	}
}
