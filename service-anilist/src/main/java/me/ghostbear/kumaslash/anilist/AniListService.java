package me.ghostbear.kumaslash.anilist;

import me.ghostbear.kumaslash.anilist.model.Media;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.graphql.client.GraphQlTransportException;
import org.springframework.graphql.client.HttpGraphQlClient;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Map;

@Service
public class AniListService {

	private static final Logger LOG = LoggerFactory.getLogger(AniListService.class);
	private static final String DOCUMENT_NAME = "findBySearchQuery";

	private final HttpGraphQlClient graphQlClient;

	public AniListService(HttpGraphQlClient graphQlClient) {
		this.graphQlClient = graphQlClient;
	}

	public Mono<Media> retrieveMedia(Type type, String query, boolean isAdult) {
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

		Type(String operationName) {
			this.operationName = operationName;
		}

		public String operationName() {
			return operationName;
		}

		public static Map<String, Object> buildVariables(String searchQuery, boolean isAdult) {
			return Map.of("searchQuery", searchQuery, "isAdult", isAdult);
		}

	}

}
