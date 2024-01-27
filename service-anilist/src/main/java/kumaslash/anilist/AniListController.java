/*
 * Copyright (C) 2023 ghostbear
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package kumaslash.anilist;

import kumaslash.anilist.model.Media;
import kumaslash.jda.annotations.EventMapping;
import kumaslash.jda.annotations.JDAController;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@JDAController
public class AniListController {

	private final AniListProcessor processor;
	private final AniListMessageEmbedFactory messageEmbedFactory;

	public AniListController(
			AniListProcessor processor, AniListMessageEmbedFactory messageEmbedFactory) {
		this.processor = processor;
		this.messageEmbedFactory = messageEmbedFactory;
	}

	@EventMapping
	public void onMessageReceived(MessageReceivedEvent event) {
		MessageChannelUnion channel = event.getChannel();
		if (!channel.getType().equals(ChannelType.TEXT)) {
			return;
		}
		if (!channel.canTalk()) {
			return;
		}
		TextChannel textChannel = channel.asTextChannel();
		Message message = event.getMessage();
		processor.process(message.getContentRaw(), textChannel.isNSFW(), state -> {
			switch (state) {
				case AniListProcessor.State.Processing ignored -> textChannel
						.sendTyping()
						.queue();
				case AniListProcessor.State.Done done -> {
					List<AniListProcessor.Result<Media>> results = done.results();
					if (results.isEmpty()) {
						message.addReaction(Emoji.fromUnicode("U+2753")).queue();
						return;
					}
					if (results.size() == 1) {
						AniListProcessor.Result<Media> result = results.getFirst();
						switch (result) {
							case AniListProcessor.Result.Found<Media> v -> {
								textChannel
										.sendMessageEmbeds(messageEmbedFactory.create(v.value()))
										.queue();
							}
							case AniListProcessor.Result.NotFound<Media>
							ignored -> message.addReaction(Emoji.fromUnicode("U+2753"))
									.queue();
						}

						return;
					}

					Map<Class<?>, List<AniListProcessor.Result<Media>>> map = results.stream()
							.collect(Collectors.groupingBy(
									AniListProcessor.Result<Media>::getClass));

					StringBuilder stringBuilder = new StringBuilder();
					for (AniListProcessor.Result<Media> mediaResult : map.getOrDefault(
							AniListProcessor.Result.Found.class, Collections.emptyList())) {
						AniListProcessor.Result.Found<Media> found =
								(AniListProcessor.Result.Found<Media>) mediaResult;
						Media media = found.value();
						stringBuilder.append(
								"- [%s](<%s>)".formatted(media.title().romaji(), media.siteUrl()));
					}

					if (map.containsKey(AniListProcessor.Result.NotFound.class)) {
						stringBuilder.append("\nNot Found:\n");
						for (AniListProcessor.Result<Media> mediaResult :
								map.get(AniListProcessor.Result.NotFound.class)) {
							AniListProcessor.Result.NotFound<Media> notFound =
									(AniListProcessor.Result.NotFound<Media>) mediaResult;
							String value = notFound.value();
							stringBuilder.append("- ").append(value);
						}
					}

					textChannel.sendMessage(stringBuilder.toString()).queue();
				}
				default -> {}
			}
		});
	}
}
