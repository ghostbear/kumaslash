/*
 * Copyright (C) 2023 ghostbear
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package kumaslash.jumbo;

import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.GetExchange;

public interface EmojiService {

	@GetExchange("/emojis/s{filename}?v=1")
	ResponseEntity<Resource> getEmoji(@PathVariable String filename);

	default ResponseEntity<Resource> getEmoji(String name, boolean animated) {
		String extension = animated ? "gif" : "png";
		return getEmoji(name + "." + extension);
	}
}
