package me.ghostbear.kumaslash.anilist;

import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.User;
import discord4j.core.object.entity.channel.TextChannel;
import discord4j.core.spec.EmbedCreateFields;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.rest.util.Color;
import me.ghostbear.core.discord4j.annotations.DiscordComponent;
import me.ghostbear.core.discord4j.annotations.DiscordEventHandler;
import me.ghostbear.kumaslash.anilist.model.Media;
import me.ghostbear.kumaslash.anilist.model.MediaStatus;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@DiscordComponent
public class AniListEventAdapter {

	private final Pattern pattern = Pattern.compile("(?:\\{\\{([\\w\\s\\d-:,!?]+)\\}\\})|(?:<<([\\w\\s\\d-:,!?]+)>>)|(?:\\[\\[([\\w\\s\\d-:,!?]+)\\]\\])");
	private final AniListService service;

	public AniListEventAdapter(AniListService service) {
		this.service = service;
	}

	@DiscordEventHandler
	public Publisher<?> onMessageCreate(MessageCreateEvent event) {
		if (event.getMessage().getAuthor().map(User::isBot).orElse(true)) {
			return Mono.empty();
		}
		String content = event.getMessage().getContent();
		return event.getMessage().getChannel()
				.filter(messageChannel -> messageChannel instanceof TextChannel)
				.map(messageChannel -> (TextChannel) messageChannel)
				.filter(messageChannel -> pattern.matcher(content).find())
				.flatMap(messageChannel -> messageChannel.type().thenReturn(messageChannel))
				.zipWhen(messageChannel -> findAndRetrieveMedia(content, messageChannel.isNsfw()).collectList())
				.filter(messageChannelAndMediaList -> !messageChannelAndMediaList.getT2().isEmpty())
				.flatMap(messageChannelAndMediaList -> messageChannelAndMediaList.getT1().createMessage(messageChannelAndMediaList.getT2().stream()
						.map(media -> EmbedCreateSpec.builder()
								.title(media.title().romaji())
								.description(cleanDescription(media.description()))
								.addField("Genre", String.join(", ", media.genres()), false)
								.image("https://img.anili.st/media/" + media.id())
								.footer(EmbedCreateFields.Footer.of(
										Arrays.stream(new Object[]{media.startDate(), visualName(media.status())})
												.filter(Objects::nonNull)
												.map(Object::toString)
												.filter(s -> !s.isEmpty())
												.collect(Collectors.joining(" - ")),
										null))
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

	private String visualName(MediaStatus status) {
 		return switch (status) {
			case FINISHED -> "Finished";
			case RELEASING -> "Releasing";
			case NOT_YET_RELEASED -> "Not Yet Released";
			case CANCELLED -> "Cancelled";
			case HIATUS -> "Hiatus";
		};
	}

	private String cleanDescription(String value) {
		if (Objects.isNull(value) || value.isEmpty()) return "No description";
		return StringUtils.abbreviate(value.replaceAll("<[brBR]{2}>", ""), 128);
	}

	private Flux<Media> findAndRetrieveMedia(String content, boolean isAdult) {
		return findMediaQueries(content)
				.parallel()
				.flatMap(typeAndSearchQuery -> service.retrieveMedia(typeAndSearchQuery, isAdult))
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
