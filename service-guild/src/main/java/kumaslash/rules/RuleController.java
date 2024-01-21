/*
 * Copyright (C) 2023 ghostbear
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package kumaslash.rules;

import kumaslash.guild.GuildCommandSupplier;
import kumaslash.guild.GuildNotifierService;
import kumaslash.jda.annotations.AutoCompleteMapping;
import kumaslash.jda.annotations.JDAController;
import kumaslash.jda.annotations.SlashCommandMapping;
import kumaslash.jda.utils.OptionMappingUtils;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import org.springframework.context.annotation.Bean;

import java.awt.Color;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@JDAController
public class RuleController {

	private final RuleService ruleService;
	private final GuildNotifierService notifierService;

	public RuleController(RuleService ruleService, GuildNotifierService notifierService) {
		this.ruleService = ruleService;
		this.notifierService = notifierService;
	}

	@SlashCommandMapping(name = "rules")
	public void rules(SlashCommandInteractionEvent event) {
		event.deferReply().queue();

		List<Rule> rules = ruleService.findAllByGuildSnowflake(event.getGuild().getIdLong());
		if (rules.isEmpty()) {
			event.getHook()
					.sendMessage(
							"The guild doesn't have any rules.\n\nIf you are the owner/admin use `/guild rule add` to add rules to the guild.")
					.delay(3, TimeUnit.SECONDS)
					.flatMap(Message::delete)
					.queue();
			return;
		}

		User user = event.getOption("target", OptionMapping::getAsUser);
		String mention = Objects.nonNull(user) ? user.getAsMention() : "";

		event.getHook().sendMessage(mention).setEmbeds(asMessageEmbed(rules)).queue();
	}

	@SlashCommandMapping(name = "rule")
	public void rule(SlashCommandInteractionEvent event) {
		event.deferReply().queue();

		User user = event.getOption("target", OptionMapping::getAsUser);
		UUID ruleId = event.getOption("rule", OptionMappingUtils::asUUID);

		Optional<Rule> optionalRule = ruleService.findOneByIdAndGuildSnowflake(
				ruleId, event.getGuild().getIdLong());

		if (optionalRule.isEmpty()) {
			event.getHook()
					.sendMessage(
							"The guild doesn't have any rule with the provided id.\n\nIf you are the owner/admin use `/guild rule add` to add the rule to the guild.")
					.delay(3, TimeUnit.SECONDS)
					.flatMap(Message::delete)
					.queue();
			return;
		}

		Rule rule = optionalRule.get();
		String mention = Objects.nonNull(user) ? user.getAsMention() : "";
		event.getHook().editOriginal(mention).setEmbeds(asMessageEmbed(rule)).queue();
	}

	@SlashCommandMapping(name = "guild rule add")
	public void guildRuleAdd(SlashCommandInteractionEvent event) {
		event.deferReply().queue();

		long snowflake = event.getGuild().getIdLong();
		Double number = event.getOption("number").getAsDouble();
		String shortDescription = event.getOption("short").getAsString();
		String longDescription = event.getOption("long").getAsString();
		Rule newRule = ruleService.save(
				new Rule(null, snowflake, number, shortDescription, longDescription));
		notifierService.notify(newRule.guildSnowflake());
		event.getHook()
				.editOriginal("Rule was added")
				.setEmbeds(
						asMessageEmbed(newRule, embedBuilder -> embedBuilder.setColor(Color.green)))
				.queue();
	}

	@SlashCommandMapping(name = "guild rule delete")
	public void guildRuleDelete(SlashCommandInteractionEvent event) {
		event.deferReply().queue();

		long guildSnowflake = event.getGuild().getIdLong();
		UUID ruleId = event.getOption("rule", OptionMappingUtils::asUUID);
		Optional<Rule> optionalRule =
				ruleService.findOneByIdAndGuildSnowflake(ruleId, guildSnowflake);
		if (optionalRule.isEmpty()) {
			event.getHook()
					.editOriginal("The guild doesn't have any rule with the provided id.")
					.delay(3, TimeUnit.SECONDS)
					.flatMap(Message::delete)
					.queue();
			return;
		}
		boolean deleted = ruleService.deleteByIdAndGuildSnowflake(ruleId, guildSnowflake);
		Rule rule = optionalRule.get();
		if (deleted) {
			notifierService.notify(guildSnowflake);
			event.getHook()
					.editOriginal("Rule was deleted")
					.setEmbeds(
							asMessageEmbed(rule, embedBuilder -> embedBuilder.setColor(Color.red)))
					.queue();
		} else {
			event.getHook()
					.editOriginal("Rule wasn't deleted")
					.setEmbeds(asMessageEmbed(
							rule, embedBuilder -> embedBuilder.setColor(Color.orange)))
					.queue();
		}
	}

	@SlashCommandMapping(name = "guild rule modify")
	public void guildRuleModify(SlashCommandInteractionEvent event) {
		event.deferReply().queue();

		long guildSnowflake = event.getGuild().getIdLong();
		UUID ruleId = event.getOption("rule", OptionMappingUtils::asUUID);
		Double number = event.getOption("number", OptionMapping::getAsDouble);
		String shortDescription = event.getOption("short", OptionMapping::getAsString);
		String longDescription = event.getOption("long", OptionMapping::getAsString);

		Optional<Rule> optionalRule =
				ruleService.findOneByIdAndGuildSnowflake(ruleId, guildSnowflake);

		if (optionalRule.isEmpty()) {
			event.getHook()
					.editOriginal("The guild doesn't have any rule with the provided id.")
					.queue();
			return;
		}

		Rule oldRule = optionalRule.get();
		Rule newRule = new Rule(
				oldRule.id(),
				oldRule.guildSnowflake(),
				Objects.requireNonNullElse(number, oldRule.number()),
				Objects.requireNonNullElse(shortDescription, oldRule.shortDescription()),
				Objects.requireNonNullElse(longDescription, oldRule.longDescription()));

		Rule savedRule = ruleService.save(newRule);
		notifierService.notify(newRule.guildSnowflake());
		event.getHook()
				.editOriginal("Rule was modified")
				.setEmbeds(asMessageEmbed(
						savedRule, embedBuilder -> embedBuilder.setColor(Color.green)))
				.queue();
	}

	@AutoCompleteMapping(value = "guild rule (delete|modify)")
	public void onCommandAutoCompleteInteraction(CommandAutoCompleteInteractionEvent event) {
		event.replyChoices(ruleService
						.findAllByGuildSnowflakeAndStartWith(
								event.getGuild().getIdLong(),
								event.getFocusedOption().getValue())
						.stream()
						.map(rule -> new Command.Choice(
								rule.shortDescription(), String.valueOf(rule.id())))
						.toList())
				.queue();
	}

	@Bean
	GuildCommandSupplier ruleGuildCommandSupplier() {
		return snowflake -> {
			List<Command.Choice> choices = ruleService.findAllByGuildSnowflake(snowflake).stream()
					.map(RuleController.this::asChoice)
					.toList();
			return Commands.slash("rule", "Display a rule of the guild")
					.addOptions(
							new OptionData(OptionType.STRING, "rule", "A rule to display", true)
									.addChoices(choices),
							new OptionData(
									OptionType.USER, "target", "Someone didn't read the rules"));
		};
	}

	@Bean
	GuildCommandSupplier rulesGuildCommandSupplier() {
		return snowflake -> Commands.slash("rules", "Display the rules of the guild")
				.addOption(OptionType.USER, "target", "Slap someone with the rulebook");
	}

	MessageEmbed asMessageEmbed(List<Rule> rules) {
		return asMessageEmbed(rules, ignored -> {});
	}

	MessageEmbed asMessageEmbed(List<Rule> rules, Consumer<EmbedBuilder> apply) {
		EmbedBuilder embedBuilder = new EmbedBuilder()
				.setTitle("Rules")
				.setDescription(rules.stream()
						.map(rule -> "%.0f **%s**. %s"
								.formatted(
										rule.number(),
										rule.shortDescription(),
										rule.longDescription()))
						.collect(Collectors.joining("\n")));
		apply.accept(embedBuilder);
		return embedBuilder.build();
	}

	MessageEmbed asMessageEmbed(Rule rule) {
		return asMessageEmbed(rule, ignored -> {});
	}

	MessageEmbed asMessageEmbed(Rule rule, Consumer<EmbedBuilder> apply) {
		EmbedBuilder embedBuilder = new EmbedBuilder()
				.setTitle("Rule %.0f".formatted(rule.number()))
				.setDescription(
						"**%s**. %s".formatted(rule.shortDescription(), rule.longDescription()))
				.setFooter("Full list of rules can be found in #rules or by using /rules.");
		apply.accept(embedBuilder);
		return embedBuilder.build();
	}

	Command.Choice asChoice(Rule rule) {
		return new Command.Choice(rule.shortDescription(), String.valueOf(rule.id()));
	}
}
