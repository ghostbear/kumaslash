package me.ghostbear.kumaslash.data.guild;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.repository.query.Param;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface GuildSocialRepository extends R2dbcRepository<GuildSocial, UUID> {

	@Query("SELECT * FROM guild_social WHERE action = :action::social_action  AND fk_guild_snowflake = :snowflake ORDER BY random() LIMIT 1")
	Mono<GuildSocial> findByGuildSnowflakeAndAction(@Param("snowflake") long snowflake, @Param("action") GuildSocial.Action action);

}
