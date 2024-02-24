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
import net.dv8tion.jda.api.entities.emoji.UnicodeEmoji;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@JDAController
public class AniListController {


	private static final UnicodeEmoji QUESTION_MARK = Emoji.fromUnicode("U+1F51E");
	private static final UnicodeEmoji NO_ONE_UNDER_EIGHTEEN = Emoji.fromUnicode("U+1F51E");

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
		boolean channelPermitsNsfw = textChannel.isNSFW();
		processor.process(message.getContentRaw(), state -> {
			switch (state) {
				case AniListProcessor.State.Processing ignored -> textChannel
						.sendTyping()
						.queue();
				case AniListProcessor.State.Done done when done.isEmpty() -> {
					message.addReaction(QUESTION_MARK).queue();
				}
				case AniListProcessor.State.Done done when done.size() == 1 -> {
					AniListProcessor.Result<Media> result = done.getFirst();
					switch (result) {
						case AniListProcessor.Result.Found<Media> v -> {
							Media media = v.value();
							if (media.isAdult() && !channelPermitsNsfw) {
								message.addReaction(NO_ONE_UNDER_EIGHTEEN).queue();
								return;
							}
							textChannel
									.sendMessageEmbeds(messageEmbedFactory.create(media))
									.queue();
						}
						case AniListProcessor.Result.NotFound<Media>
								ignored -> message.addReaction(Emoji.fromUnicode("U+2753"))
								.queue();
					}
				}
				case AniListProcessor.State.Done done -> {
					enum Groups {
						NOT_FOUND,
						FOUND,
						UNDERAGE
					}
					List<AniListProcessor.Result<Media>> results = done.results();
					Map<Groups, List<AniListProcessor.Result<Media>>> map = results.stream()
							.collect(Collectors.groupingBy(r -> switch (r) {
								case AniListProcessor.Result.Found<Media> found when found.value().isAdult() && !channelPermitsNsfw -> Groups.UNDERAGE;
								case AniListProcessor.Result.Found<Media> ignored -> Groups.FOUND;
								case AniListProcessor.Result.NotFound<Media> ignored -> Groups.NOT_FOUND;
							}));

					StringBuilder stringBuilder = new StringBuilder();
					for (AniListProcessor.Result<Media> mediaResult : map.getOrDefault(Groups.FOUND, Collections.emptyList())) {
						AniListProcessor.Result.Found<Media> found = (AniListProcessor.Result.Found<Media>) mediaResult;
						Media media = found.value();
						stringBuilder.append(
								"- [%s](<%s>)%n".formatted(media.title().romaji(), media.siteUrl()));
					}

					if (map.containsKey(Groups.UNDERAGE)) {
						stringBuilder.append("\nThere was %d NSFW item(s). This is SFW channel, keep it SFW.%n".formatted(map.get(Groups.UNDERAGE).size()));
					}

					if (map.containsKey(Groups.NOT_FOUND)) {
						stringBuilder.append("\nNot Found:\n");
						for (AniListProcessor.Result<Media> mediaResult : map.get(Groups.NOT_FOUND)) {
							AniListProcessor.Result.NotFound<Media> notFound = (AniListProcessor.Result.NotFound<Media>) mediaResult;
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
