/*
 * Copyright (C) 2023 ghostbear
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package kumaslash.core.preferences;

import java.util.function.Function;

public interface PreferenceStore {

	Preference<String> getString(String key, String defaultValue);

	Preference<Byte> getByte(String key, Byte defaultValue);

	Preference<Integer> getInteger(String key, Integer defaultValue);

	Preference<Long> getLong(String key, Long defaultValue);

	Preference<Double> getDouble(String key, Double defaultValue);

	Preference<Float> getFloat(String key, Float defaultValue);

	Preference<Boolean> getBoolean(String key, Boolean defaultValue);

	default <U> Preference<U> getObject(
			String key,
			U defaultValue,
			Function<U, String> serilizer,
			Function<String, U> deserizlier,
			U... refined) {
		Class<? extends U> aClass = (Class<? extends U>) refined.getClass().componentType();
		return getObject(key, defaultValue, serilizer, deserizlier, aClass);
	}

	<U> Preference<U> getObject(
			String key,
			U defaultValue,
			Function<U, String> serilizer,
			Function<String, U> deserizlier,
			Class<? extends U> refinedClass);

	default <U extends Enum<U>> Preference<Enum<U>> getEnum(
			String key, U defaultValue, U... refined) {
		Class<U> aClass = (Class<U>) refined.getClass().componentType();
		return getObject(key, defaultValue, Enum::name, s -> U.valueOf(aClass, s), aClass);
	}
}
