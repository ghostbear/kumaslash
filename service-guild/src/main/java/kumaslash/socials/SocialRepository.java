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
import org.springframework.data.repository.CrudRepository;

public interface SocialRepository extends CrudRepository<Social, UUID> {

	List<Social> findAllBySocialsActionId(UUID socialActionId);

	List<Social> findAllBySocialsActionIdAndUrlContainsIgnoreCase(UUID id, String query);
}
