/*
 * Copyright (C) 2023 ghostbear
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package kumaslash.user;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import kumaslash.jda.annotations.JDAController;
import kumaslash.jda.annotations.SlashCommandMapping;
import kumaslash.jda.events.CommandSupplier;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.utils.ImageProxy;
import org.springframework.context.annotation.Bean;

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

		User target = aTarget.getAsUser();
		ImageProxy avatar = aGuild ? target.getEffectiveAvatar() : target.getAvatar();

		InteractionHook interactionHook = event.getHook();
		if (Objects.isNull(avatar)) {
			interactionHook
					.sendMessageFormat("%s doesn't have a avatar", target.getEffectiveName())
					.queue();
		} else {
			interactionHook.sendMessage(avatar.getUrl(1024)).queue();
		}
	}

	@SlashCommandMapping(name = "user banner")
	public void getUserBanner(SlashCommandInteractionEvent event)
			throws ExecutionException, InterruptedException {
		event.deferReply().queue();

		OptionMapping aTarget = event.getOption("target");
		assert aTarget != null;

		User target = aTarget.getAsUser();
		String bannerUrl = target.retrieveProfile().map(User.Profile::getBannerUrl).submit().get();

		InteractionHook interactionHook = event.getHook();
		if (Objects.isNull(bannerUrl)) {
			interactionHook
					.sendMessageFormat("%s doesn't have a banner", target.getEffectiveName())
					.queue();
		} else {
			interactionHook.sendMessage(bannerUrl).queue();
		}
	}

	@Bean
	public CommandSupplier userCommandSupplier() {
		return () ->
				Commands.slash("user", "User related commands")
						.addSubcommands(
								new SubcommandData("avatar", "Get the avatar of a user")
										.addOption(
												OptionType.USER,
												"target",
												"The user to get the avatar from",
												true)
										.addOption(
												OptionType.BOOLEAN,
												"guild",
												"Get the guild avatar if user has one"),
								new SubcommandData("banner", "Get the banner of a user")
										.addOption(
												OptionType.USER,
												"target",
												"The user to get the banner from",
												true));
	}
}
