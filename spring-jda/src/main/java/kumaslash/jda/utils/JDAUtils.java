/*
 * Copyright (C) 2023 ghostbear
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package kumaslash.jda.utils;

import java.lang.reflect.Method;
import kumaslash.jda.annotations.EventMapping;
import org.springframework.core.annotation.MergedAnnotations;

public final class JDAUtils {

	private JDAUtils() {}

	public static boolean isEventMapping(Method method) {
		MergedAnnotations mergedAnnotations =
				MergedAnnotations.from(
						method, MergedAnnotations.SearchStrategy.INHERITED_ANNOTATIONS);
		return mergedAnnotations.isPresent(EventMapping.class)
				|| mergedAnnotations.isDirectlyPresent(EventMapping.class);
	}
}
