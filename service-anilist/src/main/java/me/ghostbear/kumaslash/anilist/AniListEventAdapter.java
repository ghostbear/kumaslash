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
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@DiscordComponent
public class AniListEventAdapter {

	private final AniListService service;
	private final AniListPatternMatcher matcher;

	public AniListEventAdapter(AniListService service, AniListPatternMatcher matcher) {
		this.service = service;
		this.matcher = matcher;
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
				.filter(messageChannel -> matcher.contains(content))
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
										.orElseGet(() -> {
											Media.Format format = media.format();
											if (Objects.isNull(format)) return Color.ENDEAVOUR;
											return switch (format) {
												case MANGA -> Color.JAZZBERRY_JAM;
												case NOVEL -> Color.MOON_YELLOW;
												default -> Color.ENDEAVOUR;
											};
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
		return matcher.matches(content)
				.parallel()
				.flatMap(match -> service.retrieveMedia(match.type(), match.query(), isAdult))
				.sequential();
	}

}
