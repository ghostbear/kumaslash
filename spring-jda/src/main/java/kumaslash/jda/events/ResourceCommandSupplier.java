/*
 * Copyright (C) 2023 ghostbear
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package kumaslash.jda.events;

import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.utils.data.DataObject;

import org.springframework.core.io.Resource;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

public class ResourceCommandSupplier implements CommandSupplier {

	private final Resource resource;

	public ResourceCommandSupplier(Resource resource) {
		this.resource = Objects.requireNonNull(resource);
	}

	@Override
	public CommandData get() {
		try (InputStream inputStream = resource.getInputStream()) {
			DataObject dataObject = DataObject.fromJson(inputStream);
			return CommandData.fromData(dataObject);
		} catch (IOException exception) {
			throw new RuntimeException(exception);
		}
	}
}
