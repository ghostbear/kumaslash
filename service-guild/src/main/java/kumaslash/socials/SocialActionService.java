/*
 * Copyright (C) 2023 ghostbear
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package kumaslash.socials;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Service
public class SocialActionService {

	private final SocialActionRepository socialActionRepository;

	public SocialActionService(SocialActionRepository socialActionRepository) {
		this.socialActionRepository = socialActionRepository;
	}

	public void deleteById(UUID actionId) {
		if (Objects.isNull(actionId)) {
			return;
		}
		socialActionRepository.deleteById(actionId);
	}

	@Transactional
	public SocialAction save(SocialAction aSocialAction) {
		boolean exists = socialActionRepository.existsByIdAndGuildSnowflake(
				aSocialAction.id(), aSocialAction.guildSnowflake());
		SocialAction socialAction = new SocialAction(
				aSocialAction.id(),
				aSocialAction.guildSnowflake(),
				aSocialAction.action(),
				aSocialAction.template(),
				!exists);
		return socialActionRepository.save(socialAction);
	}

	public List<SocialAction> findByGuildSnowflakeAndStartWith(long aGuildSnowflake, String value) {
		return socialActionRepository.findAllByGuildSnowflakeAndActionContainsIgnoreCase(
				aGuildSnowflake, value);
	}

	public List<SocialAction> findAllByGuildSnowflake(long guildSnowflake) {
		return socialActionRepository.findAllByGuildSnowflake(guildSnowflake);
	}

	public Optional<SocialAction> findByIdAndGuildSnowflake(UUID actionId, long guildSnowflake) {
		return socialActionRepository.findByIdAndGuildSnowflake(actionId, guildSnowflake);
	}

	public boolean existsByIdAndGuildSnowflake(UUID actionId, long guildSnowflake) {
		return socialActionRepository.existsByIdAndGuildSnowflake(actionId, guildSnowflake);
	}

	public void deleteByIdAndGuildSnowflake(UUID actionId, long guildSnowflake) {
		socialActionRepository.deleteByIdAndGuildSnowflake(actionId, guildSnowflake);
	}
}
