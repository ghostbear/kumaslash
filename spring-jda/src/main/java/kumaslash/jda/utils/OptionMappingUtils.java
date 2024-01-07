/*
 * Copyright (C) 2023 ghostbear
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package kumaslash.jda.utils;

import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.unions.GuildChannelUnion;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;

public final class OptionMappingUtils {

	private OptionMappingUtils() {}

	public static UUID asUUID(OptionMapping optionMapping) {
		String asString = optionMapping.getAsString();
		UUID uuid = UUID.fromString(asString);
		return uuid;
	}

	static <T> Optional<T> resolveOrEmpty(
			OptionMapping optionMapping, Function<OptionMapping, T> resolver) {
		return Optional.of(optionMapping).map(resolver);
	}

	public static Optional<String> asStringOrEmpty(OptionMapping optionMapping) {
		return resolveOrEmpty(optionMapping, OptionMapping::getAsString);
	}

	public static Optional<Integer> asIntOrEmpty(OptionMapping optionMapping) {
		return resolveOrEmpty(optionMapping, OptionMapping::getAsInt);
	}

	public static Optional<Long> asLongOrEmpty(OptionMapping optionMapping) {
		return resolveOrEmpty(optionMapping, OptionMapping::getAsLong);
	}

	public static Optional<Double> asDoubleOrEmpty(OptionMapping optionMapping) {
		return resolveOrEmpty(optionMapping, OptionMapping::getAsDouble);
	}

	public static Optional<Boolean> asBooleanOrEmpty(OptionMapping optionMapping) {
		return resolveOrEmpty(optionMapping, OptionMapping::getAsBoolean);
	}

	public static Optional<User> asUserOrEmpty(OptionMapping optionMapping) {
		return resolveOrEmpty(optionMapping, OptionMapping::getAsUser);
	}

	public static Optional<Member> asMemberOrEmpty(OptionMapping optionMapping) {
		return resolveOrEmpty(optionMapping, OptionMapping::getAsMember);
	}

	public static Optional<Role> asRoleOrEmpty(OptionMapping optionMapping) {
		return resolveOrEmpty(optionMapping, OptionMapping::getAsRole);
	}

	public static Optional<GuildChannelUnion> asChannelOrEmpty(OptionMapping optionMapping) {
		return resolveOrEmpty(optionMapping, OptionMapping::getAsChannel);
	}

	public static Optional<Message.Attachment> asAttachmentOrEmpty(OptionMapping optionMapping) {
		return resolveOrEmpty(optionMapping, OptionMapping::getAsAttachment);
	}

	public static Optional<IMentionable> asMentionableOrEmpty(OptionMapping optionMapping) {
		return resolveOrEmpty(optionMapping, OptionMapping::getAsMentionable);
	}

	@Override
	public boolean equals(Object obj) {
		throw new AssertionError();
	}
}
