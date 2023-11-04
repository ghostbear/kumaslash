package me.ghostbear.kumaslash.guild.repositories;

import me.ghostbear.kumaslash.guild.domain.Social;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.repository.query.Param;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface SocialRepository extends R2dbcRepository<Social, UUID> {

	@Query("SELECT * FROM guild_social WHERE action = :action::social_action  AND fk_guild_snowflake = :snowflake ORDER BY random() LIMIT 1")
	Mono<Social> findByGuildSnowflakeAndAction(@Param("snowflake") long snowflake, @Param("action") Social.Action action);

}
