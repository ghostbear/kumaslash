package me.ghostbear.kumaslash.anilist;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.SynchronousSink;

import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.function.BiFunction;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class AniListPatternMatcher {

	@NotNull
	public Flux<AniListMatch> matches(@NotNull String value) {
		return Flux.fromArray(AniListPattern.values())
				.parallel()
				.flatMap(aniListPattern -> generatorFactory(value, aniListPattern))
				.sequential();
	}

	@NotNull
	Flux<AniListMatch> generatorFactory(@NotNull String value, @NotNull AniListPattern aniListPattern) {
		return Flux.generate(getSupplier(value, aniListPattern), getGenerator())
				.map(s -> new AniListMatch(getType(aniListPattern), s));
	}

	public boolean contains(@Nullable String value) {
		String v = Objects.requireNonNullElse(value, "");
		return Arrays.stream(AniListPattern.values())
				.map(aniListPattern -> aniListPattern.getPattern().matcher(v))
				.anyMatch(Matcher::find);
	}

	@NotNull
	@Contract(pure = true)
	private static AniListService.Type getType(@NotNull AniListPattern aniListPattern) {
		return switch (aniListPattern) {
			case ANIME -> AniListService.Type.ANIME;
			case MANGA -> AniListService.Type.MANGA;
			case LIGHT_NOVEL -> AniListService.Type.LIGHT_NOVEL;
		};
	}

	@NotNull
	@Contract(pure = true)
	private static Callable<Matcher> getSupplier(@NotNull String value, @NotNull AniListPattern aniListPattern) {
		return () -> aniListPattern.getPattern().matcher(value);
	}

	@NotNull
	@Contract(pure = true)
	private static BiFunction<Matcher, SynchronousSink<String>, Matcher> getGenerator() {
		return (m, sink) -> {
			boolean found = m.find();
			if (!found) sink.complete();
			try {
				sink.next(m.group(1));
				return m;
			} catch (IllegalStateException e) {
				sink.error(e);
				return m;
			}
		};
	}

	public enum AniListPattern {
		ANIME('{', '}'),
		MANGA('<', '>'),
		LIGHT_NOVEL('[', ']');

		private final char left;
		private final char right;

		AniListPattern(char left, char right) {
			this.left = left;
			this.right = right;
		}

		public String getRawPattern() {
			// language=regex
			return "(?<![\\w\\%s])\\%s{2}(?!\\%s)(\\S(?:[^\\%s\\%s}]*\\S)?)(?<!\\%s)\\%s{2}(?![\\%s\\w])"
					.formatted(left, left, left, left, right, right, right, right);
		}

		@NotNull
		@Contract(" -> new")
		public Pattern getPattern() {
			return Pattern.compile(getRawPattern());
		}
	}

	public record AniListMatch(AniListService.Type type, String query) {
	}

}
