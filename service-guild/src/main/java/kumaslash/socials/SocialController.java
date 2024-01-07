/*
 * Copyright (C) 2023 ghostbear
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package kumaslash.socials;

import java.awt.*;
import java.time.Duration;
import java.util.*;
import java.util.List;
import java.util.concurrent.TimeUnit;
import kumaslash.jda.annotations.AutoCompleteMapping;
import kumaslash.jda.annotations.JDAController;
import kumaslash.jda.annotations.SlashCommandMapping;
import kumaslash.jda.events.CommandSupplier;
import kumaslash.jda.events.ResourceCommandSupplier;
import kumaslash.jda.utils.OptionMappingUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;

@JDAController
public class SocialController {

	private final SocialService socialService;
	private final SocialActionService socialActionService;

	public SocialController(SocialService socialService, SocialActionService socialActionService) {
		this.socialService = socialService;
		this.socialActionService = socialActionService;
	}

	@SlashCommandMapping(name = "social")
	public void social(SlashCommandInteractionEvent event) {
		User target = event.getOption("target", OptionMapping::getAsUser);
		if (event.getUser().equals(target)) {
			event.reply("Can't target yourself!")
					.setEphemeral(true)
					.delay(Duration.ofSeconds(3))
					.flatMap(InteractionHook::deleteOriginal)
					.queue();
			return;
		}
		long guildSnowflake = event.getGuild().getIdLong();
		UUID actionId = event.getOption("social_action_id", OptionMappingUtils::asUUID);

		Optional<SocialAction> socialActionOptional =
				socialActionService.findByIdAndGuildSnowflake(actionId, guildSnowflake);

		if (socialActionOptional.isEmpty()) {
			event.reply("Action doesn't exist in guild!")
					.setEphemeral(true)
					.delay(3, TimeUnit.SECONDS)
					.flatMap(InteractionHook::deleteOriginal)
					.queue();
			return;
		}
		SocialAction socialAction = socialActionOptional.get();
		List<Social> socials = socialService.findAllBySocialActionId(actionId);
		if (socials.isEmpty()) {
			event.reply(
							"Action doesn't have any images!\nIf you are an admin you can add them by using `/guild social add`.")
					.delay(3, TimeUnit.SECONDS)
					.flatMap(InteractionHook::deleteOriginal)
					.queue();
			return;
		}
		Random random = new Random();
		Social social = socials.get(random.nextInt(socials.size()));
		event.replyEmbeds(
						new EmbedBuilder()
								.setDescription(
										socialAction
												.template()
												.formatted(
														target.getAsMention(),
														event.getUser().getAsMention()))
								.setImage(social.url())
								.build())
				.queue();
	}

	@AutoCompleteMapping(value = "social")
	public void onCommandAutoCompleteInteractionC(CommandAutoCompleteInteractionEvent event) {
		event.replyChoices(
						socialActionService
								.findByGuildSnowflakeAndStartWith(
										event.getGuild().getIdLong(),
										event.getFocusedOption().getValue())
								.stream()
								.map(
										socialAction ->
												new Command.Choice(
														socialAction.action(),
														String.valueOf(socialAction.id())))
								.toList())
				.queue();
	}

	@Bean
	public CommandSupplier socialCommandSupplier() {
		return new ResourceCommandSupplier(new ClassPathResource("social.json"));
	}
}
