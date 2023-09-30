package me.ghostbear.kumaslash.guild.commands;

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.command.ApplicationCommandInteractionOption;
import discord4j.core.object.command.ApplicationCommandInteractionOptionValue;
import discord4j.core.object.entity.channel.Channel;
import me.ghostbear.core.discord4j.annotations.DiscordComponent;
import me.ghostbear.core.discord4j.annotations.DiscordInteractionHandler;
import me.ghostbear.core.discord4j.annotations.DiscordInteractionProperties;
import me.ghostbear.kumaslash.guild.GuildLogChannelRepository;
import me.ghostbear.kumaslash.guild.model.GuildLogChannel;
import reactor.core.publisher.Mono;

@DiscordComponent
public class TimeoutEventHandler {

	private final GuildLogChannelRepository guildLogChannelRepository;

	public TimeoutEventHandler(GuildLogChannelRepository guildLogChannelRepository) {
		this.guildLogChannelRepository = guildLogChannelRepository;
	}

	@DiscordInteractionProperties
	public String getName() {
		return "commands/timeout.json";
	}

	@DiscordInteractionHandler(name = "timeout.channel")
	public Mono<?> onSubcommandChannel(ChatInputInteractionEvent event) {
		Mono<Channel> channel = event.getOption("channel")
				.flatMap(option -> option.getOption("channel"))
				.flatMap(ApplicationCommandInteractionOption::getValue)
				.map(ApplicationCommandInteractionOptionValue::asChannel)
				.orElseThrow();
		return event.deferReply()
				.then(channel)
				.flatMap(c -> guildLogChannelRepository.insert(new GuildLogChannel(c.getId(), event.getInteraction().getGuildId().orElseThrow(), GuildLogChannel.Type.TIMEOUT)).thenReturn(c))
				.flatMap(c -> event.createFollowup("Timeout channel has been set to %s".formatted(c.getMention())));
	}
}
