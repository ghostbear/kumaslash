/*
 * Copyright (C) 2023 ghostbear
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package kumaslash.core.preferences;

import java.util.function.Function;
import java.util.prefs.Preferences;

public class JavaPreferenceStore implements PreferenceStore {

	private final Preferences preferences;

	public JavaPreferenceStore(Preferences preferences) {
		this.preferences = preferences;
	}

	@Override
	public Preference<String> getString(String key, String defaultValue) {
		return new JavaPreference.StringPrimitive(preferences, key, defaultValue);
	}

	@Override
	public Preference<Byte> getByte(String key, Byte defaultValue) {
		return new JavaPreference.BytePrimitive(preferences, key, defaultValue);
	}

	@Override
	public Preference<Integer> getInteger(String key, Integer defaultValue) {
		return new JavaPreference.IntegerPrimitive(preferences, key, defaultValue);
	}

	@Override
	public Preference<Long> getLong(String key, Long defaultValue) {
		return new JavaPreference.LongPrimitive(preferences, key, defaultValue);
	}

	@Override
	public Preference<Double> getDouble(String key, Double defaultValue) {
		return new JavaPreference.DoublePrimitive(preferences, key, defaultValue);
	}

	@Override
	public Preference<Float> getFloat(String key, Float defaultValue) {
		return new JavaPreference.FloatPrimitive(preferences, key, defaultValue);
	}

	@Override
	public Preference<Boolean> getBoolean(String key, Boolean defaultValue) {
		return new JavaPreference.BooleanPrimitive(preferences, key, defaultValue);
	}

	@Override
	public <U> Preference<U> getObject(
			String key,
			U defaultValue,
			Function<U, String> serilizer,
			Function<String, U> deserizlier,
			Class<? extends U> refinedClass) {
		return new JavaPreference.ObjectPrimitive<>(
				preferences, key, defaultValue, serilizer, deserizlier);
	}
}
