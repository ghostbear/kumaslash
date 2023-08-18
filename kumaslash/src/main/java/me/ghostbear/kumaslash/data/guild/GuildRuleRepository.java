package me.ghostbear.kumaslash.data.guild;

import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface GuildRuleRepository extends R2dbcRepository<GuildRule, UUID> {

	Flux<GuildRule> findByGuildSnowflake(long guildSnowflake);

	Mono<GuildRule> findByGuildSnowflakeAndIndex(long guildSnowflake, int index);

}
