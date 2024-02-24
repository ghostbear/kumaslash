/*
 * Copyright (C) 2023 ghostbear
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package kumaslash.anilist;

import kumaslash.anilist.model.Media;

import org.springframework.graphql.client.HttpGraphQlClient;
import org.springframework.stereotype.Component;

import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

@Component
public class AniListProcessor {

	private final AniListMatcher matcher;
	private final HttpGraphQlClient client;

	public AniListProcessor(AniListMatcher matcher, HttpGraphQlClient client) {
		this.matcher = matcher;
		this.client = client;
	}

	public void process(String message, Consumer<State> stateConsumer) {
		stateConsumer.accept(State.None.INSTANCE);

		if (!shouldProcess(message)) {
			stateConsumer.accept(State.Skipped.INSTANCE);
			return;
		}

		stateConsumer.accept(State.Processing.INSTANCE);

		List<Result<Media>> results = matcher.matches(message)
				.parallel()
				.map(matchResult -> {
					String operationName =
							switch (matchResult.pattern()) {
								case ANIME -> "FindAnimeBySearchQuery";
								case MANGA -> "FindMangaBySearchQuery";
								case LIGHT_NOVEL -> "FindLightNovelBySearchQuery";
							};
					return client.documentName("findBySearchQuery")
							.operationName(operationName)
							.variables(Map.of("searchQuery", matchResult.match()))
							.retrieve("Media")
							.toEntity(Media.class)
							.<Result<Media>>map(Result.Found::new)
							.switchIfEmpty(Mono.defer(
									() -> Mono.just(new Result.NotFound<>(matchResult.match()))))
							.block();
				})
				.filter(Objects::nonNull)
				.toList();
		stateConsumer.accept(new State.Done(results));
	}

	boolean shouldProcess(String message) {
		return matcher.contains(message);
	}

	public sealed interface Result<T> {
		record NotFound<T>(String value) implements Result<T> {}

		record Found<T>(T value) implements Result<T> {}
	}

	public sealed interface State {
		enum None implements State {
			INSTANCE
		}

		enum Processing implements State {
			INSTANCE
		}

		record Done(List<Result<Media>> results) implements State {

			public Done {
				results = Objects.requireNonNullElseGet(results, Collections::emptyList);
			}

			public int size() {
				return results.size();
			}

			public boolean isEmpty() {
				return results.isEmpty();
			}

			public Result<Media> getFirst() {
				return results.getFirst();
			}
		}

		enum Skipped implements State {
			INSTANCE
		}
	}
}
