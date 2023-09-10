package me.ghostbear.kumaslash.guild.commands;

import discord4j.common.util.Snowflake;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.command.ApplicationCommandInteractionOption;
import discord4j.core.object.command.ApplicationCommandInteractionOptionValue;
import discord4j.core.object.entity.User;
import discord4j.core.spec.EmbedCreateFields;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.discordjson.possible.Possible;
import discord4j.rest.util.Color;
import me.ghostbear.core.discord4j.annotations.DiscordComponent;
import me.ghostbear.core.discord4j.annotations.DiscordInteractionHandler;
import me.ghostbear.core.discord4j.annotations.DiscordInteractionProperties;
import me.ghostbear.kumaslash.guild.GuildRuleRepository;
import reactor.core.publisher.Mono;

import java.util.Optional;

@DiscordComponent
public class RulesEventHandler {

	private final GuildRuleRepository ruleRepository;

	public RulesEventHandler(GuildRuleRepository ruleRepository) {
		this.ruleRepository = ruleRepository;
	}

	@DiscordInteractionProperties
	public String getName() {
		return "commands/rules.json";
	}

	@DiscordInteractionHandler(name = "rules")
	public Mono<Void> handle(ChatInputInteractionEvent event) {
		Optional<Long> index = event.getOption("index")
				.flatMap(ApplicationCommandInteractionOption::getValue)
				.map(ApplicationCommandInteractionOptionValue::asLong);
		if (index.isPresent()) {
			return event.deferReply()
					.then(ruleRepository.findByGuildSnowflakeAndIndex(event.getInteraction().getGuildId().map(Snowflake::asLong).orElseThrow(), index.get().intValue()))
					.zipWith(event.getOption("user")
							.flatMap(ApplicationCommandInteractionOption::getValue)
							.map(ApplicationCommandInteractionOptionValue::asUser)
							.orElse(Mono.empty())
							.map(User::getMention)
							.map(Possible::of)
							.switchIfEmpty(Mono.just(Possible.absent())))
					.flatMap(ruleAndMention -> event.createFollowup()
							.withContent(ruleAndMention.getT2())
							.withEmbeds(EmbedCreateSpec.builder()
									.color(Color.BISMARK)
									.title("Server Rule")
									.description("%s. **%s** %s".formatted(ruleAndMention.getT1().index(), ruleAndMention.getT1().title(), ruleAndMention.getT1().description()))
									.footer(EmbedCreateFields.Footer.of("Full list of rules can be found in #rules or by using /rules.", null))
									.build()))
					.then();
		} else {
			return event.deferReply()
					.then(ruleRepository.findByGuildSnowflake(event.getInteraction().getGuildId().map(Snowflake::asLong).orElseThrow())
							.reduce("", (out, rule) -> out + """
										%s. **%s** %s

									""".formatted(rule.index(), rule.title(), rule.description())))
					.zipWith(event.getOption("user")
							.flatMap(ApplicationCommandInteractionOption::getValue)
							.map(ApplicationCommandInteractionOptionValue::asUser)
							.orElse(Mono.empty())
							.map(User::getMention)
							.map(Possible::of)
							.switchIfEmpty(Mono.just(Possible.absent())))
					.flatMap(rulesAndMention -> event.createFollowup()
							.withContent(rulesAndMention.getT2())
							.withEmbeds(EmbedCreateSpec.builder()
									.color(Color.BISMARK)
									.title("Server Rules")
									.description(rulesAndMention.getT1().trim())
									.build()))
					.then();
		}
	}

}
