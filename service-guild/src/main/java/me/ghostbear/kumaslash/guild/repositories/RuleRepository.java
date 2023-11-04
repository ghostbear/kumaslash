package me.ghostbear.kumaslash.guild.repositories;

import me.ghostbear.kumaslash.guild.domain.Rule;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface RuleRepository extends R2dbcRepository<Rule, UUID> {

	Flux<Rule> findByGuildSnowflake(long guildSnowflake);

	Mono<Rule> findByGuildSnowflakeAndIndex(long guildSnowflake, int index);

}
