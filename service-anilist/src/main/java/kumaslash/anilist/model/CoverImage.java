/*
 * Copyright (C) 2023 ghostbear
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package kumaslash.anilist.model;

import java.awt.*;
import java.util.Objects;

public record CoverImage(String color) {

	public Color asColor(Color defaultColor) {
		if (Objects.isNull(color)) {
			return defaultColor;
		}
		return Color.decode(color);
	}
}
