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
import java.util.Optional;
import java.util.UUID;
import kumaslash.jda.annotations.AutoCompleteMapping;
import kumaslash.jda.annotations.JDAController;
import kumaslash.jda.annotations.SlashCommandMapping;
import kumaslash.jda.utils.OptionMappingUtils;
import kumaslash.utils.Duad;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;

@JDAController
public class SocialModerationController {

	private final SocialService socialService;
	private final SocialActionService socialActionService;

	private final Object[] names = {"John", "Jane"};
	private final SocialTemplateValidator templateValidator = new SocialTemplateValidator(names);

	public SocialModerationController(
			SocialService socialService, SocialActionService socialActionService) {
		this.socialService = socialService;
		this.socialActionService = socialActionService;
	}

	@SlashCommandMapping(name = "guild social-action add")
	public void addSocialAction(SlashCommandInteractionEvent event) {
		long guildSnowflake = event.getGuild().getIdLong();
		String actionName = event.getOption("name", OptionMapping::getAsString);
		String template = event.getOption("template", OptionMapping::getAsString);
		if (!templateValidator.test(template)) {
			event.reply(
							"%s is either missing or is more than two. Example of valid templates '%s got slapped' or '%s got slapped by %s'")
					.delay(Duration.ofSeconds(3))
					.flatMap(InteractionHook::deleteOriginal)
					.queue();
			return;
		}
		SocialAction saved =
				socialActionService.save(
						new SocialAction(null, guildSnowflake, actionName, template));
		event.replyEmbeds(
						new EmbedBuilder()
								.setTitle("Action Saved")
								.setColor(Color.GREEN)
								.setDescription(saved.template().formatted(names))
								.setImage("https://picsum.photos/300/170")
								.build())
				.queue();
	}

	@SlashCommandMapping(name = "guild social-action modify")
	public void putSocialAction(SlashCommandInteractionEvent event) {
		long guildSnowflake = event.getGuild().getIdLong();
		UUID actionId = event.getOption("action_id", OptionMappingUtils::asUUID);
		Optional<String> actionOrEmpty =
				event.getOption("name", Optional.empty(), OptionMappingUtils::asStringOrEmpty);
		Optional<String> templateOrEmpty =
				event.getOption("template", Optional.empty(), OptionMappingUtils::asStringOrEmpty);

		Optional<SocialAction> oldActionOrEmpty =
				socialActionService.findByIdAndGuildSnowflake(actionId, guildSnowflake);
		if (oldActionOrEmpty.isEmpty()) {
			event.replyFormat("Social action#%s doesn't exist", actionId)
					.delay(Duration.ofSeconds(3))
					.flatMap(InteractionHook::deleteOriginal)
					.queue();
			return;
		}

		SocialAction oldAction = oldActionOrEmpty.get();
		SocialAction newAction =
				socialActionService.save(
						new SocialAction(
								actionId,
								guildSnowflake,
								actionOrEmpty.orElse(oldAction.action()),
								templateOrEmpty.orElse(oldAction.template())));

		event.replyEmbeds(
						new EmbedBuilder()
								.setTitle("Action Modified")
								.setColor(Color.GREEN)
								.setDescription(newAction.template().formatted(names))
								.setImage("https://picsum.photos/300/170")
								.build())
				.queue();
	}

	@SlashCommandMapping(name = "guild social-action delete")
	public void deleteSocialAction(SlashCommandInteractionEvent event) {
		long guildSnowflake = event.getGuild().getIdLong();
		UUID actionId = event.getOption("action_id", OptionMappingUtils::asUUID);
		if (!socialActionService.existsByIdAndGuildSnowflake(actionId, guildSnowflake)) {
			event.replyFormat("Social action#%s doesn't exist", actionId)
					.delay(Duration.ofSeconds(3))
					.flatMap(InteractionHook::deleteOriginal)
					.queue();
			return;
		}

		socialActionService.deleteByIdAndGuildSnowflake(actionId, guildSnowflake);
		event.replyFormat("Social action#%s deleted", actionId).queue();
	}

	@SlashCommandMapping(name = "guild social add")
	public void addSocial(SlashCommandInteractionEvent event) {
		long guildSnowflake = event.getGuild().getIdLong();
		UUID actionId = event.getOption("action_id", OptionMappingUtils::asUUID);
		String socialUrl = event.getOption("url", OptionMapping::getAsString);

		Optional<SocialAction> socialActionOrEmpty =
				socialActionService.findByIdAndGuildSnowflake(actionId, guildSnowflake);
		if (socialActionOrEmpty.isEmpty()) {
			event.replyFormat("Social action#%s doesn't exist", actionId)
					.delay(Duration.ofSeconds(3))
					.flatMap(InteractionHook::deleteOriginal)
					.queue();
			return;
		}

		SocialAction socialAction = socialActionOrEmpty.get();
		Social newSocial = socialService.save(new Social(null, actionId, socialUrl, true));
		event.replyEmbeds(
						new EmbedBuilder()
								.setTitle("Social Saved")
								.setColor(Color.GREEN)
								.setDescription(socialAction.template().formatted(names))
								.setImage(newSocial.url())
								.build())
				.queue();
	}

	@SlashCommandMapping(name = "guild social delete")
	public void removeSocial(SlashCommandInteractionEvent event) {
		UUID socialId = event.getOption("social_id", OptionMappingUtils::asUUID);

		if (!socialService.existsById(socialId)) {
			event.replyFormat("Social#%s doesn't exist", socialId)
					.delay(Duration.ofSeconds(3))
					.flatMap(InteractionHook::deleteOriginal)
					.queue();
			return;
		}

		socialService.deleteById(socialId);
		event.replyFormat("Social#%s deleted", socialId).queue();
	}

	@AutoCompleteMapping(value = "guild social-action (modify|delete)")
	public void onCommandAutoCompleteInteraction(CommandAutoCompleteInteractionEvent event) {
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

	@AutoCompleteMapping(value = "guild social add")
	public void onCommandAutoCompleteInteractionA(CommandAutoCompleteInteractionEvent event) {
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

	@AutoCompleteMapping(value = "guild social delete")
	public void onCommandAutoCompleteInteractionB(CommandAutoCompleteInteractionEvent event) {
		event.replyChoices(
						socialActionService
								.findAllByGuildSnowflake(event.getGuild().getIdLong())
								.stream()
								.flatMap(
										socialAction ->
												socialService
														.findAllBySocialActionIdAndUrl(
																socialAction.id(),
																event.getFocusedOption().getValue())
														.stream()
														.map(
																social ->
																		new Duad<>(
																				social,
																				socialAction)))
								.map(
										duad ->
												new Command.Choice(
														duad.left(SocialAction::action)
																+ ": "
																+ duad.right(Social::url),
														String.valueOf(duad.right(Social::id))))
								.toList())
				.queue();
	}
}
