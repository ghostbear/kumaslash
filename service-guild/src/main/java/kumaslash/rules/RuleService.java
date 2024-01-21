/*
 * Copyright (C) 2023 ghostbear
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package kumaslash.rules;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Service
public class RuleService {

	private final RuleRepository ruleRepository;

	public RuleService(RuleRepository ruleRepository) {
		this.ruleRepository = ruleRepository;
	}

	@Transactional
	public Rule save(Rule aRule) {
		boolean exists = Objects.nonNull(aRule.getId()) && ruleRepository.existsById(aRule.id());
		Rule rule = new Rule(
				aRule.getId(),
				aRule.guildSnowflake(),
				aRule.number(),
				aRule.shortDescription(),
				aRule.longDescription(),
				!exists);
		Rule saved = ruleRepository.save(rule);
		ruleRepository.reorderNumber(aRule.guildSnowflake());
		return ruleRepository.findById(saved.id()).orElseThrow();
	}

	public List<Rule> findAllByGuildSnowflake(Long snowflake) {
		if (Objects.isNull(snowflake)) {
			return Collections.emptyList();
		}
		return ruleRepository.findAllByGuildSnowflakeOrderByNumber(snowflake);
	}

	public List<Rule> findAllByGuildSnowflakeAndStartWith(Long snowflake, String query) {
		if (Objects.isNull(snowflake)) {
			return Collections.emptyList();
		}
		return ruleRepository.findAllByGuildSnowflakeAndShortDescriptionContainsIgnoreCase(
				snowflake, query);
	}

	public Optional<Rule> findOneByIdAndGuildSnowflake(UUID ruleId, long guildSnowflake) {
		if (Objects.isNull(ruleId)) {
			return Optional.empty();
		}
		return ruleRepository.findRuleByIdAndGuildSnowflake(ruleId, guildSnowflake);
	}

	@Transactional
	public boolean deleteByIdAndGuildSnowflake(UUID ruleId, long guildSnowflake) {
		if (Objects.isNull(ruleId)) {
			return false;
		}
		boolean deleted = ruleRepository.deleteByIdAndGuildSnowflake(ruleId, guildSnowflake);
		ruleRepository.reorderNumber(guildSnowflake);
		return deleted;
	}
}
