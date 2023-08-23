package me.ghostbear.discord.bot;

import discord4j.common.util.Snowflake;
import discord4j.core.event.domain.interaction.UserInteractionEvent;
import me.ghostbear.core.discord4j.annotations.DiscordInteractionHandler;
import me.ghostbear.core.discord4j.annotations.DiscordApplicationCommandProperties;
import me.ghostbear.core.discord4j.annotations.DiscordComponent;
import reactor.core.publisher.Mono;

import java.time.Instant;

@DiscordComponent
public class MyDiscordUserComponent {

	@DiscordApplicationCommandProperties
	public String properties() {
		// language=json
		return """
				{
				  "name": "Timeout User",
				  "type": 2
				}
				""";
	}

	@DiscordInteractionHandler(name = "Timeout User")
	public Mono<?> bop(UserInteractionEvent event) {
		return event.deferReply()
				.withEphemeral(true)
				.then(event.getTargetUser())
				.flatMap(user -> user.asMember(event.getInteraction().getGuildId().orElse(Snowflake.of(-1))))
				.flatMap(member -> member.edit().withCommunicationDisabledUntilOrNull(Instant.now().plusSeconds(10)).then(Mono.defer(() -> event.createFollowup("Timeout user for 10 seconds"))));
	}

}
