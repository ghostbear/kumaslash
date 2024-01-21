/*
 * Copyright (C) 2023 ghostbear
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package kumaslash.ping;

import kumaslash.jda.annotations.JDAController;
import kumaslash.jda.annotations.SlashCommandMapping;
import kumaslash.jda.events.CommandSupplier;
import kumaslash.jda.events.ResourceCommandSupplier;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;

import java.time.Duration;
import java.time.Instant;

@JDAController
public class PingController {

	@SlashCommandMapping(name = "ping")
	public void ping(SlashCommandInteractionEvent event) {
		Instant now = Instant.now();
		event.reply("Pong!")
				.setEphemeral(true)
				.flatMap(interactionHook -> interactionHook.editOriginalFormat(
						"Pong! (%d ms)", Duration.between(now, Instant.now()).toMillis()))
				.queue();
	}

	@Bean
	public CommandSupplier pingCommandSupplier() {
		return new ResourceCommandSupplier(new ClassPathResource("ping.json"));
	}
}
