package me.ghostbear.kumaslash.anilist;

import me.ghostbear.kumaslash.anilist.model.Media;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.graphql.client.GraphQlTransportException;
import org.springframework.graphql.client.HttpGraphQlClient;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Service
public class AniListService {

	@NotNull
	private static final Logger LOG = LoggerFactory.getLogger(AniListService.class);
	@NotNull
	private static final String DOCUMENT_NAME = "findBySearchQuery";

	@NotNull
	private final HttpGraphQlClient graphQlClient;

	public AniListService(@NotNull HttpGraphQlClient graphQlClient) {
		this.graphQlClient = Objects.requireNonNull(graphQlClient);
	}

	@NotNull
	public Mono<Media> retrieveMedia(@NotNull Type type, @Nullable String query, @Nullable Boolean isAdult) {
		return graphQlClient.documentName(DOCUMENT_NAME)
				.operationName(type.operationName())
				.variables(AniListService.Type.buildVariables(query, isAdult))
				.retrieve("Media")
				.toEntity(Media.class)
				.doOnError(GraphQlTransportException.class, throwable -> LOG.error("Failed to retrieve media, 404 Not Found means that AniList can't find a title with the provided search query and can safely be ignored", throwable))
				.onErrorComplete();
	}

	public enum Type {
		ANIME("FindAnimeBySearchQuery"),
		MANGA("FindMangaBySearchQuery"),
		LIGHT_NOVEL("FindLightNovelBySearchQuery");

		private final String operationName;

		Type(@NotNull String operationName) {
			this.operationName = operationName;
		}

		@NotNull
		public String operationName() {
			return operationName;
		}

		@NotNull
		public static Map<String, Object> buildVariables(@Nullable String searchQuery, @Nullable Boolean isAdult) {
			Map<String, Object> variables = new HashMap<>(2);
			if (Objects.nonNull(searchQuery)) variables.put("searchQuery", searchQuery);
			if (Objects.nonNull(searchQuery)) variables.put("isAdult", isAdult);
			return variables;
		}

	}

}
