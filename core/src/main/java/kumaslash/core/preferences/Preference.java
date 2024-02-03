/*
 * Copyright (C) 2023 ghostbear
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package kumaslash.core.preferences;

public interface Preference<T> {

	String key();

	T get();

	void set(T value);

	boolean isSet();

	void delete();

	T defaultValue();
}
