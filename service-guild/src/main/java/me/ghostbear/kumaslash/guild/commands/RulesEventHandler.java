package me.ghostbear.kumaslash.guild.commands;

import discord4j.common.util.Snowflake;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.command.ApplicationCommandInteractionOption;
import discord4j.core.object.command.ApplicationCommandInteractionOptionValue;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.User;
import discord4j.core.spec.EmbedCreateFields;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.core.spec.InteractionCallbackSpecDeferReplyMono;
import discord4j.discordjson.possible.Possible;
import discord4j.rest.util.Color;
import me.ghostbear.core.discord4j.DiscordInteractionPropertySupplier;
import me.ghostbear.core.discord4j.annotations.DiscordComponent;
import me.ghostbear.core.discord4j.annotations.DiscordInteractionHandler;
import me.ghostbear.core.discord4j.annotations.DiscordInteractionProperties;
import me.ghostbear.kumaslash.guild.GuildRuleRepository;
import me.ghostbear.kumaslash.guild.model.GuildRule;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@DiscordComponent
public class RulesEventHandler {

	private final GuildRuleRepository ruleRepository;

	public RulesEventHandler(GuildRuleRepository ruleRepository) {
		this.ruleRepository = ruleRepository;
	}

	@DiscordInteractionProperties
	public DiscordInteractionPropertySupplier commandProperties() {
		return () -> "commands/rules.json";
	}

	@DiscordInteractionHandler(name = "rules")
	public Publisher<?> handle(ChatInputInteractionEvent event) {
		Snowflake snowflake = event.getInteraction().getGuildId().orElseThrow();
		Optional<Long> index = event.getOption("index")
				.flatMap(ApplicationCommandInteractionOption::getValue)
				.map(ApplicationCommandInteractionOptionValue::asLong);
		Mono<User> user = event.getOption("user")
				.flatMap(ApplicationCommandInteractionOption::getValue)
				.map(ApplicationCommandInteractionOptionValue::asUser)
				.orElse(Mono.empty());
		Flux<GuildRule> guildRules = getRules(snowflake, index);
		Mono<Possible<String>> mention = user.map(User::getMention)
				.map(Possible::of)
				.switchIfEmpty(Mono.just(Possible.absent()));
		return event.deferReply()
				.thenMany(guildRules)
				.collectList()
				.zipWith(mention)
				.flatMap(tuple -> {
					if (tuple.getT1().size() > 1) {
						return rulesReply(event, tuple.getT1(), tuple.getT2());
					}
					return ruleReply(event,  tuple.getT1().get(0), tuple.getT2());
				});
	}

	Mono<Message> rulesReply(ChatInputInteractionEvent event, List<GuildRule> guildRules, Possible<String> mention) {
		return event.createFollowup()
				.withContent(mention)
				.withEmbeds(
						EmbedCreateSpec.builder()
								.color(Color.BISMARK)
								.title("Server Rules")
								.description(guildRules.stream()
										.map(RulesEventHandler.this::getFormatted)
										.collect(Collectors.joining("\n\n")))
								.build());
	}

	Mono<Message> ruleReply(ChatInputInteractionEvent event, GuildRule guildRule, Possible<String> mention) {
		return event.createFollowup()
				.withContent(mention)
				.withEmbeds(
						EmbedCreateSpec.builder()
								.color(Color.BISMARK)
								.title("Server Rule")
								.description(getFormatted(guildRule))
								.footer(EmbedCreateFields.Footer.of("Full list of rules can be found in #rules or by using /rules.", null))
								.build());
	}

	String getFormatted(GuildRule guildRule) {
		return "%s. **%s** %s".formatted(guildRule.index(), guildRule.title(), guildRule.description());
	}

	Flux<GuildRule> getRules(Snowflake snowflake, Optional<Long> index) {
		return Mono.justOrEmpty(index)
				.flatMap(i -> ruleRepository.findByGuildSnowflakeAndIndex(snowflake.asLong(), i.intValue()))
				.flux()
				.switchIfEmpty(Flux.defer(() -> ruleRepository.findByGuildSnowflake(snowflake.asLong())));
	}

}
