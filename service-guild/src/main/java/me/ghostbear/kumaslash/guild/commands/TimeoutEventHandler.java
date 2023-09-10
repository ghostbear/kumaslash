package me.ghostbear.kumaslash.guild.commands;

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.command.ApplicationCommandInteractionOption;
import discord4j.core.object.command.ApplicationCommandInteractionOptionValue;
import me.ghostbear.core.discord4j.annotations.DiscordInteractionProperties;
import me.ghostbear.core.discord4j.annotations.DiscordComponent;
import me.ghostbear.core.discord4j.annotations.DiscordInteractionHandler;
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
	public Mono<Void> onSubcommandChannel(ChatInputInteractionEvent event) {
		return event.deferReply()
				.then(Mono.defer(() -> event.getOption("channel")
						.flatMap(option -> option.getOption("channel"))
						.flatMap(ApplicationCommandInteractionOption::getValue)
						.map(ApplicationCommandInteractionOptionValue::asChannel)
						.orElseThrow()))
				.flatMap(channel -> guildLogChannelRepository.insert(new GuildLogChannel(channel.getId(), event.getInteraction().getGuildId().orElseThrow(), GuildLogChannel.Type.TIMEOUT)).thenReturn(channel))
				.flatMap(channel -> event.createFollowup("Timeout channel has been set to %s".formatted(channel.getMention())))
				.then();
	}
}
