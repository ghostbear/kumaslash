/*
 * Copyright (C) 2023 ghostbear
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package kumaslash.anilist;

import kumaslash.anilist.model.FuzzyDate;
import kumaslash.anilist.model.Media;

import kumaslash.anilist.model.MediaStatus;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class AniListMessageEmbedFactory {

	private static final String ELLIPSES = Character.toString(0x2026);
	public static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy/MM/dd");

	public MessageEmbed create(Media media) {
		EmbedBuilder builder = new EmbedBuilder();
		builder.setTitle(media.title().romaji());
		builder.setUrl(media.siteUrl());
		description(builder, media);
		genre(builder, media);
		image(builder, media);
		color(builder, media);
		footer(builder, media);
		return builder.build();
	}

	void description(EmbedBuilder builder, Media media) {
		String description = media.description();
		if (Objects.isNull(description)) {
			return;
		}
		if (description.isEmpty()) {
			return;
		}
		description = description.replaceAll("<[brBR]{2}>", "");
		if (description.length() >= 128) {
			description = description.substring(0, 128) + ELLIPSES;
		}
		builder.setDescription(description);
	}

	void genre(EmbedBuilder builder, Media media) {
		List<String> genres = media.genres();
		if (Objects.isNull(genres)) {
			return;
		}
		if (genres.isEmpty()) {
			return;
		}
		builder.addField("Genre", String.join(", ", genres), false);
	}

	void image(EmbedBuilder builder, Media media) {
		Long id = media.id();
		if (Objects.isNull(id)) {
			return;
		}
		builder.setImage("https://img.anili.st/media/" + id);
	}

	void color(EmbedBuilder builder, Media media) {
		Color defaultColor =
				switch (media.format()) {
					case null -> Color.GRAY;
					case TV, TV_SHORT, MOVIE, SPECIAL, OVA, ONA, MUSIC -> Color.GREEN;
					case MANGA, ONE_SHOT -> Color.ORANGE;
					case NOVEL -> Color.MAGENTA;
				};
		if (Objects.isNull(media.coverImage())) {
			builder.setColor(defaultColor);
			return;
		}
		Color color = media.coverImage().asColor(defaultColor);
		builder.setColor(color);
	}

	void footer(EmbedBuilder builder, Media media) {
		if (Objects.isNull(media.startDate()) && Objects.isNull(media.status())) {
			return;
		}
		String footer = Arrays.stream(new Object[] {media.startDate(), media.status()})
				.filter(Objects::nonNull)
				.map(o -> {
					if (o instanceof FuzzyDate fuzzyDate) {
						return fuzzyDate.asLocalDate();
					}
					return o;
				})
				.map(object -> switch (object) {
					case LocalDate localDate -> DATE_FORMAT.format(localDate);
					case MediaStatus status -> switch (status) {
						case FINISHED -> "Finished";
						case RELEASING -> "Releasing";
						case NOT_YET_RELEASED -> "Not Yet Released";
						case CANCELLED -> "Cancelled";
						case HIATUS -> "Hiatus";
					};
					default -> String.valueOf(object);
				})
				.filter(Predicate.not(String::isEmpty))
				.collect(Collectors.joining(" - "));
		builder.setFooter(footer);
	}
}
