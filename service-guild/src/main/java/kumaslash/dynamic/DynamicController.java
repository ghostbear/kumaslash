/*
 * Copyright (C) 2023 ghostbear
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package kumaslash.dynamic;

import java.util.List;
import kumaslash.guild.GuildCommandSupplier;
import kumaslash.guild.GuildNotifierService;
import kumaslash.jda.annotations.JDAController;
import kumaslash.jda.annotations.SlashCommandMapping;
import kumaslash.jda.events.CommandSupplier;
import kumaslash.jda.events.ResourceCommandSupplier;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.transaction.annotation.Transactional;

@JDAController
public class DynamicController {

	private final GuildNotifierService notifierService;
	private final DynamicRepository dynamicRepository;

	public DynamicController(
			GuildNotifierService notifierService, DynamicRepository dynamicRepository) {
		this.notifierService = notifierService;
		this.dynamicRepository = dynamicRepository;
	}

	@SlashCommandMapping(name = "dynamic")
	public void dynamic(SlashCommandInteractionEvent event) {
		String value = event.getOption("value").getAsString();
		event.reply(value.toUpperCase()).queue();
	}

	@Transactional
	@SlashCommandMapping(name = "module-dynamic")
	public void moduleDynamic(SlashCommandInteractionEvent event) {
		event.deferReply().queue();

		String shout = event.getOption("value").getAsString();
		long snowflake = event.getGuild().getIdLong();

		dynamicRepository.save(new Dynamic(shout.hashCode() + snowflake, snowflake, shout));
		notifierService.notify(snowflake);
		event.getHook().editOriginalFormat("Will value %s", shout).queue();
	}

	@Bean
	public CommandSupplier dynamicCommandSupplier() {
		return new ResourceCommandSupplier(new ClassPathResource("dynamic.json"));
	}

	@Bean
	GuildCommandSupplier dynamicGuildCommandSupplier() {
		return snowflake -> {
			List<Dynamic> dynamics = dynamicRepository.findAllBySnowflake(snowflake);
			return Commands.slash("dynamic", "A dynamic command")
					.addOptions(
							new OptionData(OptionType.STRING, "value", "A predefined value")
									.addChoices(
											dynamics.stream()
													.map(
															dynamic ->
																	new Command.Choice(
																			dynamic.value(),
																			dynamic.value()))
													.toList()));
		};
	}
}
