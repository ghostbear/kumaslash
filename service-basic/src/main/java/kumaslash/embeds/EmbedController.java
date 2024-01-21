/*
 * Copyright (C) 2023 ghostbear
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package kumaslash.embeds;

import kumaslash.core.Duad;
import kumaslash.jda.annotations.JDAController;
import kumaslash.jda.annotations.SlashCommandMapping;
import kumaslash.jda.events.CommandSupplier;
import kumaslash.jda.events.ResourceCommandSupplier;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.SelfUser;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.unions.GuildChannelUnion;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.utils.data.DataObject;

import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;

@JDAController
public class EmbedController {

	@SlashCommandMapping(name = "embed create")
	public void createEmbed(SlashCommandInteractionEvent event) {
		Message.Attachment json = event.getOption("json", OptionMapping::getAsAttachment);
		GuildChannelUnion channel = event.getOption("channel", OptionMapping::getAsChannel);

		DataObject dataObject =
				json.getProxy().download().thenApply(DataObject::fromJson).join();

		if (channel == null) {
			event.reply("Couldn't find text channel").queue();
			return;
		}

		TextChannel textChannel = channel.asTextChannel();
		if (!textChannel.canTalk()) {
			event.reply("Can't send message in channel").queue();
			return;
		}
		textChannel
				.sendMessageEmbeds(EmbedBuilder.fromData(dataObject).build())
				.flatMap(__ -> event.reply("created embed"))
				.queue();
	}

	@SlashCommandMapping(name = "embed modify")
	public void modifyEmbed(SlashCommandInteractionEvent event) {
		Message.Attachment json = event.getOption("json", OptionMapping::getAsAttachment);
		Duad<String, String> messageId = event.getOption("message", optionMapping -> {
			String asString = optionMapping.getAsString();
			String[] split = asString.split("-");
			if (split.length != 2) {
				return null;
			}
			return Duad.of(split);
		});

		DataObject dataObject =
				json.getProxy().download().thenApply(DataObject::fromJson).join();

		JDA jda = event.getJDA();
		SelfUser selfUser = jda.getSelfUser();

		Guild guild = event.getGuild();
		if (guild == null) {
			event.reply("This command only works in guilds").queue();
			return;
		}
		TextChannel textChannel =
				guild.getTextChannelById(messageId.left(Long::valueOf).longValue());
		if (textChannel == null || !textChannel.canTalk()) {
			event.reply("Can't find channel or can't send message in it").queue();
			return;
		}
		textChannel
				.retrieveMessageById(messageId.right(Long::valueOf).longValue())
				.flatMap(message -> {
					if (message != null && message.getAuthor().equals(selfUser)) {
						return message.editMessageEmbeds(
										EmbedBuilder.fromData(dataObject).build())
								.flatMap(__ -> event.reply("Message edited"));
					}
					return event.reply("Can't edit message, the message isn't from me");
				})
				.onErrorFlatMap(throwable -> event.reply("Failed to modify embed"))
				.queue();
	}

	@Bean
	public CommandSupplier embedCommandSupplier() {
		return new ResourceCommandSupplier(new ClassPathResource("embed.json"));
	}
}
