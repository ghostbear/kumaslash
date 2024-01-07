/*
 * Copyright (C) 2023 ghostbear
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package kumaslash.socials;

import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class SocialService {

	private final SocialRepository socialRepository;

	public SocialService(SocialRepository socialRepository) {
		this.socialRepository = socialRepository;
	}

	public Social save(Social social) {
		return socialRepository.save(social);
	}

	public void deleteById(UUID id) {
		socialRepository.deleteById(id);
	}

	public List<Social> findAllBySocialActionId(UUID socialActionId) {
		return socialRepository.findAllBySocialsActionId(socialActionId);
	}

	public List<Social> findAllBySocialActionIdAndUrl(UUID id, String query) {
		return socialRepository.findAllBySocialsActionIdAndUrlContainsIgnoreCase(id, query);
	}

	public boolean existsById(UUID socialId) {
		return socialRepository.existsById(socialId);
	}
}
