/*
 * Copyright (C) 2023 ghostbear
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package kumaslash.jumbo;

import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import kumaslash.jda.annotations.JDAController;
import kumaslash.jda.annotations.SlashCommandMapping;
import kumaslash.jda.events.CommandSupplier;
import kumaslash.jda.events.ResourceCommandSupplier;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.utils.FileUpload;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;

@JDAController
public class JumboController {

	private static final Pattern pattern =
			Pattern.compile("<(?<animated>a)?\\:(?<name>\\w+):(?<snowflake>\\d+)>");

	private final JumboService jumboService;

	public JumboController(JumboService jumboService) {
		this.jumboService = jumboService;
	}

	@SlashCommandMapping(name = "jumbo")
	public void jumbo(SlashCommandInteractionEvent event) {
		event.deferReply().queue();

		String emoji = event.getOption("emoji", OptionMapping::getAsString);
		assert emoji != null;
		Matcher matcher = pattern.matcher(emoji);

		if (!matcher.matches()) {
			event.getHook()
					.editOriginal("Option isn't an emoji")
					.delay(3, TimeUnit.SECONDS)
					.flatMap(Message::delete)
					.queue();
			return;
		}

		boolean animated = matcher.group("animated") != null;
		String name = matcher.group("name");
		String snowflake = matcher.group("snowflake");

		Optional<FileUpload> rawEmoji = jumboService.getRawEmoji(snowflake, name, animated);
		if (rawEmoji.isEmpty()) {
			event.getHook()
					.editOriginal("Failed to find emoji")
					.delay(3, TimeUnit.SECONDS)
					.flatMap(Message::delete)
					.queue();
			return;
		}

		event.getHook().sendFiles(rawEmoji.get()).queue();
	}

	@Bean
	public CommandSupplier jumboCommandSupplier() {
		return new ResourceCommandSupplier(new ClassPathResource("jumbo.json"));
	}
}
