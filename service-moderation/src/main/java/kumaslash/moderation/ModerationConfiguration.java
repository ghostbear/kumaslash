/*
 * Copyright (C) 2023 ghostbear
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package kumaslash.moderation;

import kumaslash.core.preferences.JavaPreferenceStore;
import kumaslash.core.preferences.PreferenceStore;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import java.util.prefs.Preferences;

@Configuration
public class ModerationConfiguration {

	@Bean
	@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
	Preferences preferences() {
		return Preferences.userRoot().node("/kumaslash/moderation");
	}

	@Bean
	PreferenceStore moderationPreferenceStore(Preferences preferences) {
		return new JavaPreferenceStore(preferences.node("guild"));
	}
}
