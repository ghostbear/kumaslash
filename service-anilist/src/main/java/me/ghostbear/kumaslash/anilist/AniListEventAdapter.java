package me.ghostbear.kumaslash.anilist;

import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.User;
import discord4j.core.object.entity.channel.TextChannel;
import discord4j.core.spec.EmbedCreateFields;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.discordjson.possible.Possible;
import discord4j.rest.util.Color;
import me.ghostbear.core.discord4j.annotations.DiscordComponent;
import me.ghostbear.core.discord4j.annotations.DiscordEventHandler;
import me.ghostbear.kumaslash.anilist.model.CoverImage;
import me.ghostbear.kumaslash.anilist.model.Media;
import me.ghostbear.kumaslash.anilist.model.MediaStatus;
import me.ghostbear.kumaslash.anilist.model.Title;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@DiscordComponent
public class AniListEventAdapter {

	@NotNull
	private final AniListService service;
	@NotNull
	private final AniListPatternMatcher matcher;

	public AniListEventAdapter(@NotNull AniListService service, @NotNull AniListPatternMatcher matcher) {
		this.service = Objects.requireNonNull(service);
		this.matcher = Objects.requireNonNull(matcher);
	}

	@DiscordEventHandler
	public Publisher<?> onMessageCreate(@NotNull MessageCreateEvent event) {
		Message message = event.getMessage();
		if (message.getAuthor().map(User::isBot).orElse(true)) {
			return Mono.empty();
		}
		String content = message.getContent();
		return message.getChannel()
				.filter(channel -> channel instanceof TextChannel)
				.map(channel -> (TextChannel) channel)
				.filter(textChannel -> matcher.contains(content))
				.flatMap(textChannel -> textChannel.type().thenReturn(textChannel))
				.zipWhen(textChannel -> findAndRetrieveMedia(content, textChannel.isNsfw()).collectList())
				.filter(messageChannelAndMediaList -> !messageChannelAndMediaList.getT2().isEmpty())
				.flatMap(textChannelAndMediaList -> successMessage(textChannelAndMediaList.getT1(), textChannelAndMediaList.getT2()));
	}

	@NotNull
	Mono<Message> successMessage(@NotNull TextChannel textChannel, @NotNull List<Media> mediaList) {
		var uniqueMediaSet = new HashSet<>(mediaList);
		if (uniqueMediaSet.size() > 3) {
			String stringBuilder = mediaList.stream().map(media -> "- [%s](<%s>)\n".formatted(getTitle(media.title()).orElseThrow(), media.siteUrl())).collect(Collectors.joining());
            return textChannel.createMessage(stringBuilder.trim());
		}
		return textChannel.createMessage(
				uniqueMediaSet.stream()
						.map(AniListEventAdapter.this::createMessage)
						.toArray(EmbedCreateSpec[]::new));
	}

	@NotNull
	EmbedCreateSpec createMessage(@NotNull Media media) {
		return EmbedCreateSpec.builder()
				.url(getSiteUrl(media.siteUrl()))
				.title(getTitle(media.title()).map(Possible::of).orElse(Possible.absent()))
				.description(getDescription(media.description()))
				.addField("Genre", getGenres(media.genres()).toOptional().orElse("No genre"), false)
				.image(getEmbedImage(media.id()))
				.color(getColor(media.coverImage(), media.format()))
				.footer(
						EmbedCreateFields.Footer.of(
								Arrays.stream(new Object[]{media.startDate(), visualName(media.status())})
										.filter(Objects::nonNull)
										.map(Object::toString)
										.filter(s -> !s.isEmpty())
										.collect(Collectors.joining(" - ")),
								null))
				.build();
	}

	@NotNull
	private Possible<String> getEmbedImage(Long id) {
		if (Objects.isNull(id)) return Possible.absent();
		return Possible.of("https://img.anili.st/media/" + id);
	}

	private Optional<String> getTitle(@Nullable Title title) {
		Optional<Title> mediaOptional = Optional.ofNullable(title);
		return mediaOptional.map(Title::romaji)
				.or(() -> mediaOptional.map(Title::english));
	}

	@NotNull
	private Possible<String> getSiteUrl(@Nullable String siteUrl) {
		return Optional.ofNullable(siteUrl)
				.map(Possible::of)
				.orElse(Possible.absent());
	}

	@NotNull
	private Possible<String> getGenres(@Nullable List<String> genres) {
		if (Objects.isNull(genres)) {
			return Possible.absent();
		}
		String s = String.join(", ", genres);
		return Possible.of(s);
	}

	@NotNull
	private Possible<Color> getColor(@Nullable CoverImage coverImage, @Nullable Media.Format mediaFormat) {
		Supplier<Color> colorSupplier = () -> getDefaultColor(mediaFormat);
		if (Objects.isNull(coverImage)) return Possible.of(colorSupplier.get());
		return Possible.of(coverImage.asDiscord4jColor().orElseGet(colorSupplier));
	}

	@NotNull
	private Color getDefaultColor(@Nullable Media.Format mediaFormat) {
		return switch (mediaFormat) {
			case null -> Color.GRAY;
			case MANGA -> Color.JAZZBERRY_JAM;
			case NOVEL -> Color.MOON_YELLOW;
			default -> Color.ENDEAVOUR;
		};
	}

	@NotNull
	private String visualName(@Nullable MediaStatus status) {
		return switch (status) {
			case null -> "Unknown";
			case FINISHED -> "Finished";
			case RELEASING -> "Releasing";
			case NOT_YET_RELEASED -> "Not Yet Released";
			case CANCELLED -> "Cancelled";
			case HIATUS -> "Hiatus";
		};
	}

	@NotNull
	private String getDescription(@Nullable String value) {
		return Optional.ofNullable(value)
				.filter(s ->  !s.isEmpty())
				.map(s -> s.replaceAll("<[brBR]{2}>", ""))
				.map(s -> StringUtils.abbreviate(s, 128))
				.orElse("No description");
	}

	@NotNull
	private Flux<Media> findAndRetrieveMedia(@NotNull String content, @Nullable Boolean isAdult) {
		return matcher.matches(content)
				.parallel()
				.flatMap(match -> service.retrieveMedia(match.type(), match.query(), isAdult))
				.sequential();
	}

}
