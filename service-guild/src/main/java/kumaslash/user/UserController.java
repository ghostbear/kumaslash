/*
 * Copyright (C) 2023 ghostbear
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package kumaslash.user;

import java.awt.*;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import kumaslash.jda.annotations.JDAController;
import kumaslash.jda.annotations.SlashCommandMapping;
import kumaslash.jda.events.CommandSupplier;
import kumaslash.jda.events.ResourceCommandSupplier;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.utils.ImageProxy;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;

@JDAController
public class UserController {

	@SlashCommandMapping(name = "user avatar")
	public void getUserAvatar(SlashCommandInteractionEvent event) {
		event.deferReply().queue();

		OptionMapping aTarget = event.getOption("target");
		assert aTarget != null;

		boolean aGuild =
				Optional.ofNullable(event.getOption("guild"))
						.map(OptionMapping::getAsBoolean)
						.orElse(false);

		User targetUser = aTarget.getAsUser();
		Member targetMember = aTarget.getAsMember();
		ImageProxy avatar;
		if (aGuild) {
			avatar = targetMember.getEffectiveAvatar() != null ? targetMember.getEffectiveAvatar() : targetUser.getEffectiveAvatar();
		} else {
			avatar = targetUser.getEffectiveAvatar();
		}
		InteractionHook interactionHook = event.getHook();
		if (Objects.isNull(avatar)) {
			interactionHook
					.sendMessageFormat("%s doesn't have a avatar", targetMember.getEffectiveName())
					.delay(3, TimeUnit.SECONDS)
					.flatMap(Message::delete)
					.queue();
		} else {
			Color accentColor = targetUser.retrieveProfile().map(User.Profile::getAccentColor).complete();
			interactionHook
					.sendMessageEmbeds(
							new EmbedBuilder()
									.setImage(avatar.getUrl(2048))
									.setColor(accentColor)
									.setFooter("Avatar for " + targetMember.getEffectiveName())
									.build())
					.queue();
		}
	}

	@SlashCommandMapping(name = "user banner")
	public void getUserBanner(SlashCommandInteractionEvent event) {
		event.deferReply().queue();

		OptionMapping aTarget = event.getOption("target");
		assert aTarget != null;

		User targetUser = aTarget.getAsUser();
		Member targetMember = aTarget.getAsMember();
		String bannerUrl = targetUser.retrieveProfile().map(User.Profile::getBannerUrl).complete();

		InteractionHook interactionHook = event.getHook();
		if (Objects.isNull(bannerUrl)) {
			interactionHook
					.sendMessageFormat("%s doesn't have a banner", targetMember.getEffectiveName())
					.delay(3, TimeUnit.SECONDS)
					.flatMap(Message::delete)
					.queue();
		} else {
			Color accentColor = targetUser.retrieveProfile().map(User.Profile::getAccentColor).complete();
			interactionHook
					.sendMessageEmbeds(
							new EmbedBuilder()
									.setImage(bannerUrl)
									.setColor(accentColor)
									.setFooter("Banner for " + targetMember.getEffectiveName())
									.build())
					.queue();
			interactionHook.sendMessage(bannerUrl).queue();
		}
	}

	@Bean
	public CommandSupplier userCommandSupplier() {
		return new ResourceCommandSupplier(new ClassPathResource("user.json"));
	}
}
