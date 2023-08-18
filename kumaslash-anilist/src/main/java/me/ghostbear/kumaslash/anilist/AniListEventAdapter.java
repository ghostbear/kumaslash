package me.ghostbear.kumaslash.anilist;

import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.ReactiveEventAdapter;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.User;
import discord4j.core.spec.EmbedCreateFields;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.rest.util.Color;
import me.ghostbear.kumaslash.anilist.model.Media;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.reactivestreams.Publisher;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class AniListEventAdapter extends ReactiveEventAdapter {

	private final Pattern pattern = Pattern.compile("(?:\\{\\{([\\w\\s\\d-:,!?]+)\\}\\})|(?:<<([\\w\\s\\d-:,!?]+)>>)|(?:\\[\\[([\\w\\s\\d-:,!?]+)\\]\\])");
	private final AniListService service;

	public AniListEventAdapter(GatewayDiscordClient client, AniListService service) {
		this.service = service;
		client.on(this).subscribe();
	}

	@Override
	public Publisher<?> onMessageCreate(MessageCreateEvent event) {
		if (event.getMessage().getAuthor().map(User::isBot).orElse(true)) {
			return Mono.empty();
		}
		String content = event.getMessage().getContent();
		return event.getMessage().getChannel()
				.filter(messageChannel -> pattern.matcher(content).find())
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
				.parallel()
				.flatMap(service::retrieveMedia)
				.sequential();
	}

	private Flux<ImmutablePair<AniListService.Type, String>> findMediaQueries(String content) {
		Matcher matcher = pattern.matcher(content);
		List<ImmutablePair<AniListService.Type, String>> results = new ArrayList<>();
		while (matcher.find()) {
			if (Objects.nonNull(matcher.group(1))) {
				results.add(ImmutablePair.of(AniListService.Type.ANIME, matcher.group(1)));
			} else if (Objects.nonNull(matcher.group(2))) {
				results.add(ImmutablePair.of(AniListService.Type.MANGA, matcher.group(2)));
			} else if (Objects.nonNull(matcher.group(3))) {
				results.add(ImmutablePair.of(AniListService.Type.LIGHT_NOVEL, matcher.group(3)));
			}
		}
		return Flux.fromIterable(results);
	}

}
