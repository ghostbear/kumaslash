/*
 * Copyright (C) 2023 ghostbear
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package kumaslash.moderation;

import kumaslash.core.preferences.Preference;
import kumaslash.jda.annotations.JDAController;
import kumaslash.jda.annotations.SlashCommandMapping;
import kumaslash.jda.events.CommandSupplier;
import kumaslash.jda.events.ResourceCommandSupplier;

import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.unions.GuildChannelUnion;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;

import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;

import java.util.Objects;

@JDAController
public class ModerationController {

	private final ModerationPreferences preferences;

	public ModerationController(ModerationPreferences preferences) {
		this.preferences = preferences;
	}

	@SlashCommandMapping(name = "moderation channel set")
	public void onSetModerationChannel(SlashCommandInteractionEvent event) {
		GuildChannelUnion channelUnion = event.getOption("channel", OptionMapping::getAsChannel);
		assert channelUnion != null;
		if (!(channelUnion instanceof TextChannel)) {
			event.reply("Channel must be a text channel").queue();
			return;
		}
		if (Objects.isNull(event.getGuild())) {
			event.reply("Can't set moderation channel for private messages").queue();
			return;
		}
		Preference<Long> moderationChannel = preferences.moderationChannel(event.getGuild());
		moderationChannel.set(channelUnion.getIdLong());
		event.replyFormat("Moderation channel has been set to %s", channelUnion.getAsMention())
				.queue();
	}

	@Bean
	public CommandSupplier moderationCommandSupplier() {
		return new ResourceCommandSupplier(new ClassPathResource("moderation.json"));
	}
}
