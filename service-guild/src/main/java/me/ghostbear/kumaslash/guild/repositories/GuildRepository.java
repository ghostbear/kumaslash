package me.ghostbear.kumaslash.guild.repositories;

import me.ghostbear.kumaslash.guild.domain.Guild;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.repository.query.Param;
import reactor.core.publisher.Mono;

public interface GuildRepository extends R2dbcRepository<Guild, Long> {

	@Query("INSERT INTO guild VALUES (:snowflake) ON CONFLICT DO NOTHING")
	Mono<Void> saveOrDoNothing(@Param("snowflake") long snowflake);

}
