package me.ghostbear.kumaslash.guild;

import me.ghostbear.kumaslash.guild.model.Guild;
import org.springframework.data.r2dbc.repository.R2dbcRepository;

public interface GuildRepository extends R2dbcRepository<Guild, Long> {

}
