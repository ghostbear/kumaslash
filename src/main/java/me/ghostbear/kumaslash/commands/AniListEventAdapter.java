package me.ghostbear.kumaslash.commands;

import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.ReactiveEventAdapter;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.User;
import discord4j.core.spec.EmbedCreateFields;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.rest.util.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import me.ghostbear.kumaslash.data.anilist.Media;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.graphql.client.GraphQlTransportException;
import org.springframework.graphql.client.HttpGraphQlClient;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class AniListEventAdapter extends ReactiveEventAdapter {

	private static final Logger LOG = LoggerFactory.getLogger(AniListEventAdapter.class);
	private static final String DOCUMENT_NAME = "findBySearchQuery";

	private final Pattern pattern = Pattern.compile("(?:\\{\\{([\\w\\s\\d-:,!?]+)\\}\\})|(?:<<([\\w\\s\\d-:,!?]+)>>)|(?:\\[\\[([\\w\\s\\d-:,!?]+)\\]\\])");
	private final HttpGraphQlClient graphQlClient;

	public AniListEventAdapter(GatewayDiscordClient client, HttpGraphQlClient graphQlClient) {
		this.graphQlClient = graphQlClient;
		client.on(this).subscribe();
	}

	@Override
	public Publisher<?> onMessageCreate(MessageCreateEvent event) {
		if (event.getMessage().getAuthor().map(User::isBot).orElse(true)) {
            return Mono.empty();
        }
		String content = event.getMessage().getContent();
		return event.getMessage().getChannel()
				.filter(messageChannel -> content.matches(pattern.pattern()))
				.flatMap(messageChannel -> messageChannel.type().thenReturn(messageChannel))
				.zipWith(findAndRetrieveMedia(content).collectList())
				.filter(messageChannelAndMediaList -> !messageChannelAndMediaList.getT2().isEmpty())
				.flatMap(messageChannelAndMediaList -> messageChannelAndMediaList.getT1().createMessage(messageChannelAndMediaList.getT2().stream()
						.map(media -> EmbedCreateSpec.builder()
								.title(media.title().romaji())
								.description(cleanDescription(media.description()))
								.addField("Genre", String.join(", ", media.genres()),false)
								.image("https://img.anili.st/media/" + media.id())
								.footer(EmbedCreateFields.Footer.of("%s - %s".formatted(media.startDate(), media.status()), null))
								.color(media.coverImage()
										.asDiscord4jColor()
										.orElseGet(() -> switch (media.format()) {
											case MANGA -> Color.JAZZBERRY_JAM;
											case NOVEL -> Color.MOON_YELLOW;
											default -> Color.ENDEAVOUR;
										}))
								.url(media.siteUrl())
								.build())
						.toArray(EmbedCreateSpec[]::new)));
	}

	private String cleanDescription(String value) {
		return StringUtils.abbreviate(value.replaceAll("<br>", ""), 128);
	}

	private Flux<Media> findAndRetrieveMedia(String content) {
		return findMediaQueries(content)
				.parallel(2)
				.flatMap(this::retrieveMedia)
				.sequential();
	}

	private Mono<Media> retrieveMedia(ImmutablePair<Type, String> typeAndSearchQuery) {
		return graphQlClient.documentName(DOCUMENT_NAME)
				.operationName(typeAndSearchQuery.getLeft().documentName())
				.variables(Type.buildVariables(typeAndSearchQuery.getRight()))
				.retrieve("Media")
				.toEntity(Media.class)
				.doOnError(GraphQlTransportException.class, throwable -> LOG.warn("Failed to retrieve media, 404 Not Found means that AniList can't find a title with the provided search query and can safely be ignored", throwable))
				.onErrorComplete();
	}

	private Flux<ImmutablePair<Type, String>> findMediaQueries(String content) {
		Matcher matcher = pattern.matcher(content);
		List<ImmutablePair<Type, String>> results = new ArrayList<>();
		while (matcher.find()) {
			if (Objects.nonNull(matcher.group(1))) {
				results.add(ImmutablePair.of(Type.ANIME, matcher.group(1)));
			} else if (Objects.nonNull(matcher.group(2))) {
				results.add(ImmutablePair.of(Type.MANGA, matcher.group(2)));
			} else if (Objects.nonNull(matcher.group(3))) {
				results.add(ImmutablePair.of(Type.LIGHT_NOVEL, matcher.group(3)));
			}
		}
		return Flux.fromIterable(results);
	}

	private enum Type {
		ANIME("FindAnimeBySearchQuery"),
		MANGA("FindMangaBySearchQuery"),
		LIGHT_NOVEL("FindLightNovelBySearchQuery");

		private final String documentName;

		Type(String documentName) {
			this.documentName = documentName;
		}

		public String documentName() {
			return documentName;
		}

		public static Map<String, Object> buildVariables(String searchQuery) {
			return Map.of("searchQuery", searchQuery);
		}

	}

}
