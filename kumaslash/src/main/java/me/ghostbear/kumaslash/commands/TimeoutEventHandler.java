package me.ghostbear.kumaslash.commands;

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.command.ApplicationCommandInteractionOption;
import discord4j.core.object.command.ApplicationCommandInteractionOptionValue;
import me.ghostbear.kumaslash.commands.core.SlashCommandEventHandler;
import me.ghostbear.kumaslash.data.guild.GuildLogChannel;
import me.ghostbear.kumaslash.data.guild.GuildLogChannelRepository;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class TimeoutEventHandler implements SlashCommandEventHandler.SubSlashCommand {

	private final GuildLogChannelRepository guildLogChannelRepository;

	public TimeoutEventHandler(GuildLogChannelRepository guildLogChannelRepository) {
		this.guildLogChannelRepository = guildLogChannelRepository;
	}

	@Override
	public String getName() {
		return "timeout";
	}

	@Override
	public String getSubName() {
		return "channel";
	}

	@Override
	public Mono<Void> handle(ChatInputInteractionEvent event, ApplicationCommandInteractionOption option) {
		return event.deferReply()
				.then(Mono.defer(() -> option.getOption("channel")
						.flatMap(ApplicationCommandInteractionOption::getValue)
						.map(ApplicationCommandInteractionOptionValue::asChannel)
						.orElseThrow()))
				.flatMap(channel -> guildLogChannelRepository.insert(new GuildLogChannel(channel.getId(), event.getInteraction().getGuildId().orElseThrow(), GuildLogChannel.Type.TIMEOUT)).thenReturn(channel))
				.flatMap(channel -> event.createFollowup("Timeout channel has been set to %s".formatted(channel.getMention())))
				.then();
	}
}
