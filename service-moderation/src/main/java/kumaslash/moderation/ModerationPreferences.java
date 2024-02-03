/*
 * Copyright (C) 2023 ghostbear
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package kumaslash.moderation;

import kumaslash.core.preferences.Preference;
import kumaslash.core.preferences.PreferenceStore;

import net.dv8tion.jda.api.entities.Guild;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class ModerationPreferences {

	private final PreferenceStore preferenceStore;

	public ModerationPreferences(
			@Qualifier("moderationPreferenceStore") PreferenceStore preferenceStore) {
		this.preferenceStore = preferenceStore;
	}

	public Preference<Long> moderationChannel(Guild key) {
		return preferenceStore.getLong(key.getId() + "-moderation-channel-id", 0L);
	}
}
