package me.ghostbear.kumaslash.anilist;

import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.SynchronousSink;

import java.util.Arrays;
import java.util.concurrent.Callable;
import java.util.function.BiFunction;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class AniListPatternMatcher {

	public Flux<AniListMatch> matches(String value) {
		return Flux.fromArray(AniListPattern.values())
				.parallel()
				.flatMap(aniListPattern ->
						Flux.generate(getSupplier(value, aniListPattern), getGenerator())
								.map(s -> new AniListMatch(getType(aniListPattern), s)))
				.sequential();
	}

	public boolean contains(String value) {
		return Arrays.stream(AniListPattern.values())
				.map(aniListPattern -> aniListPattern.getPattern().matcher(value))
				.anyMatch(Matcher::find);
	}

	private static AniListService.Type getType(AniListPattern aniListPattern) {
		return switch (aniListPattern) {
			case ANIME -> AniListService.Type.ANIME;
			case MANGA -> AniListService.Type.MANGA;
			case LIGHT_NOVEL -> AniListService.Type.LIGHT_NOVEL;
		};
	}

	private static Callable<Matcher> getSupplier(String value, AniListPattern aniListPattern) {
		return () -> aniListPattern.getPattern().matcher(value);
	}

	private static BiFunction<Matcher, SynchronousSink<String>, Matcher> getGenerator() {
		return (m, sink) -> {
			boolean found = m.find();
			if (!found) sink.complete();
			sink.next(m.group());
			return m;
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

		public Pattern getPattern() {
			return Pattern.compile(getRawPattern());
		}
	}

	public record AniListMatch(AniListService.Type type, String query) {
	}

}
