/*
 * Copyright (C) 2023 ghostbear
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package kumaslash.jumbo;

import java.io.InputStream;
import java.util.Optional;
import net.dv8tion.jda.api.utils.FileUpload;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.function.ThrowingSupplier;

@Service
public class JumboService {

	private final EmojiService emojiService;

	public JumboService(EmojiService emojiService) {
		this.emojiService = emojiService;
	}

	public Optional<FileUpload> getRawEmoji(String snowflake, String name, boolean animated) {
		ResponseEntity<Resource> responseEntity = emojiService.getEmoji(snowflake, animated);
		if (!responseEntity.getStatusCode().is2xxSuccessful()) {
			return Optional.empty();
		}
		String extension = animated ? "gif" : "png";
		String filename = name + "." + extension;
		FileUpload fileUpload =
				FileUpload.fromStreamSupplier(
						filename,
						ThrowingSupplier.of(
								() -> {
									if (!responseEntity.hasBody()) {
										return InputStream.nullInputStream();
									}
									assert responseEntity.getBody() != null;
									return responseEntity.getBody().getInputStream();
								}));
		return Optional.of(fileUpload);
	}
}
