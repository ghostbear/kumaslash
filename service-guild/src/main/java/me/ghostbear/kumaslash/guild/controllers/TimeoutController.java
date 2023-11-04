package me.ghostbear.kumaslash.guild.controllers;

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.command.ApplicationCommandInteractionOption;
import discord4j.core.object.command.ApplicationCommandInteractionOptionValue;
import me.ghostbear.core.discord4j.utils.Resources;
import me.ghostbear.core.discord4j.annotations.DiscordComponent;
import me.ghostbear.core.discord4j.annotations.DiscordInteractionHandler;
import me.ghostbear.core.discord4j.annotations.DiscordInteractionProperties;
import me.ghostbear.kumaslash.guild.repositories.ChannelRepository;
import me.ghostbear.kumaslash.guild.domain.Channel;
import reactor.core.publisher.Mono;

@DiscordComponent
public class TimeoutController {

	private final ChannelRepository channelRepository;

	public TimeoutController(ChannelRepository channelRepository) {
		this.channelRepository = channelRepository;
	}

	@DiscordInteractionProperties
	public Resources commandProperties() {
		return Resources.of("commands/timeout.json");
	}

	@DiscordInteractionHandler(name = "timeout.channel")
	public Mono<?> onSubcommandChannel(ChatInputInteractionEvent event) {
		Mono<discord4j.core.object.entity.channel.Channel> channel = event.getOption("channel")
				.flatMap(option -> option.getOption("channel"))
				.flatMap(ApplicationCommandInteractionOption::getValue)
				.map(ApplicationCommandInteractionOptionValue::asChannel)
				.orElseThrow();
		return event.deferReply()
				.then(channel)
				.flatMap(c -> channelRepository.insert(new Channel(c.getId(), event.getInteraction().getGuildId().orElseThrow(), Channel.Type.TIMEOUT)).thenReturn(c))
				.flatMap(c -> event.createFollowup("Timeout channel has been set to %s".formatted(c.getMention())));
	}
}
