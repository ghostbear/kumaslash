/*
 * Copyright (C) 2023 ghostbear
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package kumaslash.rules;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RuleRepository extends CrudRepository<Rule, UUID> {

	List<Rule> findAllByGuildSnowflakeOrderByNumber(long guildSnowflake);

	List<Rule> findAllByGuildSnowflakeAndShortDescriptionContainsIgnoreCase(
			long guildSnowflake, String query);

	Optional<Rule> findRuleByIdAndGuildSnowflake(UUID id, long guildSnowflake);

	@Modifying
	@Query("DELETE FROM rules WHERE id = :id AND guild_snowflake = :guildSnowflake")
	boolean deleteByIdAndGuildSnowflake(UUID id, long guildSnowflake);

	@Modifying
	@Query(
			"""
			WITH row AS (SELECT id, (row_number() over (ORDER BY number)) number FROM rules WHERE guild_snowflake = :guildSnowflake)
			UPDATE rules
			SET
			number = row.number FROM row
			WHERE row.id = rules.id;
			""")
	void reorderNumber(Long guildSnowflake);
}
