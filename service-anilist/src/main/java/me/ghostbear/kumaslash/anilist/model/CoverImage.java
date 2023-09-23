package me.ghostbear.kumaslash.anilist.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import discord4j.rest.util.Color;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

@JsonIgnoreProperties(ignoreUnknown = true)
public record CoverImage(@Nullable String color) {

	public Optional<Color> asDiscord4jColor() {
		return Optional.ofNullable(color)
				.map(value -> value.replace("#", ""))
				.map(value -> Integer.parseInt(value, 16))
				.map(Color::of);
	}

}
