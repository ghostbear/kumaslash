/*
 * Copyright (C) 2023 ghostbear
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package kumaslash.core.preferences;

import java.util.Arrays;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

public abstract class JavaPreference<T> implements Preference<T> {

	private final Preferences preferences;
	private final String key;
	private final T defaultValue;

	protected JavaPreference(Preferences preferences, String key, T defaultValue) {
		this.preferences = preferences;
		this.key = key;
		this.defaultValue = defaultValue;
	}

	@Override
	public String key() {
		return key;
	}

	@Override
	public T get() {
		if (!isSet()) {
			return defaultValue;
		}
		return getValue(preferences, key, defaultValue);
	}

	@Override
	public void set(T value) {
		Consumer<Preferences> preferencesConsumer = setValue(key, value);
		preferencesConsumer.accept(preferences);
	}

	@Override
	public boolean isSet() {
		try {
			return Arrays.asList(preferences.keys()).contains(key);
		} catch (BackingStoreException e) {
			return false;
		}
	}

	@Override
	public void delete() {
		preferences.remove(key);
	}

	@Override
	public T defaultValue() {
		return defaultValue;
	}

	abstract T getValue(Preferences preferences, String key, T defaultValue);

	abstract Consumer<Preferences> setValue(String key, T value);

	public static class StringPrimitive extends JavaPreference<String> {

		public StringPrimitive(Preferences preferences, String key, String defaultValue) {
			super(preferences, key, defaultValue);
		}

		@Override
		String getValue(Preferences preferences, String key, String defaultValue) {
			return preferences.get(key, defaultValue);
		}

		@Override
		Consumer<Preferences> setValue(String key, String value) {
			return preferences -> preferences.put(key, value);
		}
	}

	public static class IntegerPrimitive extends JavaPreference<Integer> {

		public IntegerPrimitive(Preferences preferences, String key, Integer defaultValue) {
			super(preferences, key, defaultValue);
		}

		@Override
		Integer getValue(Preferences preferences, String key, Integer defaultValue) {
			return preferences.getInt(key, defaultValue);
		}

		@Override
		Consumer<Preferences> setValue(String key, Integer value) {
			return preferences -> preferences.putInt(key, value);
		}
	}

	public static class LongPrimitive extends JavaPreference<Long> {

		public LongPrimitive(Preferences preferences, String key, Long defaultValue) {
			super(preferences, key, defaultValue);
		}

		@Override
		Long getValue(Preferences preferences, String key, Long defaultValue) {
			return preferences.getLong(key, defaultValue);
		}

		@Override
		Consumer<Preferences> setValue(String key, Long value) {
			return preferences -> preferences.putLong(key, value);
		}
	}

	public static class BytePrimitive extends JavaPreference<Byte> {

		public BytePrimitive(Preferences preferences, String key, Byte defaultValue) {
			super(preferences, key, defaultValue);
		}

		@Override
		Byte getValue(Preferences preferences, String key, Byte defaultValue) {
			return Byte.valueOf(preferences.get(key, String.valueOf(defaultValue)));
		}

		@Override
		Consumer<Preferences> setValue(String key, Byte value) {
			return preferences -> preferences.put(key, String.valueOf(value));
		}
	}

	public static class DoublePrimitive extends JavaPreference<Double> {

		protected DoublePrimitive(Preferences preferences, String key, Double defaultValue) {
			super(preferences, key, defaultValue);
		}

		@Override
		Double getValue(Preferences preferences, String key, Double defaultValue) {
			return preferences.getDouble(key, defaultValue);
		}

		@Override
		Consumer<Preferences> setValue(String key, Double value) {
			return preferences -> preferences.putDouble(key, value);
		}
	}

	public static class FloatPrimitive extends JavaPreference<Float> {

		protected FloatPrimitive(Preferences preferences, String key, Float defaultValue) {
			super(preferences, key, defaultValue);
		}

		@Override
		Float getValue(Preferences preferences, String key, Float defaultValue) {
			return preferences.getFloat(key, defaultValue);
		}

		@Override
		Consumer<Preferences> setValue(String key, Float value) {
			return preferences -> preferences.putFloat(key, value);
		}
	}

	public static class BooleanPrimitive extends JavaPreference<Boolean> {

		protected BooleanPrimitive(Preferences preferences, String key, Boolean defaultValue) {
			super(preferences, key, defaultValue);
		}

		@Override
		Boolean getValue(Preferences preferences, String key, Boolean defaultValue) {
			return preferences.getBoolean(key, defaultValue);
		}

		@Override
		Consumer<Preferences> setValue(String key, Boolean value) {
			return null;
		}
	}

	public static class ObjectPrimitive<T> extends JavaPreference<T> {

		private final Function<T, String> serilizer;
		private final Function<String, T> deserilizer;

		public ObjectPrimitive(
				Preferences preferences,
				String key,
				T defaultValue,
				Function<T, String> serilizer,
				Function<String, T> deserilizer) {
			super(preferences, key, defaultValue);
			this.serilizer = serilizer;
			this.deserilizer = deserilizer;
		}

		@Override
		T getValue(Preferences preferences, String key, T defaultValue) {
			return deserilizer.apply(preferences.get(key, serilizer.apply(defaultValue)));
		}

		@Override
		Consumer<Preferences> setValue(String key, T value) {
			return preferences -> preferences.put(key, serilizer.apply(value));
		}
	}
}
