/*
 * Copyright (C) 2023 ghostbear
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package kumaslash.socials;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface SocialActionRepository extends CrudRepository<SocialAction, UUID> {

	boolean existsByIdAndGuildSnowflake(UUID id, long guildSnowflake);

	List<SocialAction> findAllByGuildSnowflakeAndActionContainsIgnoreCase(
			long guildSnowflake, String query);

	List<SocialAction> findAllByGuildSnowflake(long guildSnowflake);

	Optional<SocialAction> findByIdAndGuildSnowflake(UUID actionId, long guildSnowflake);

	@Modifying
	@Query("delete from socials_action a where a.id = :id and a.guild_snowflake = :guild_snowflake")
	void deleteByIdAndGuildSnowflake(
			@Param("id") UUID actionId, @Param("guild_snowflake") long guildSnowflake);
}
