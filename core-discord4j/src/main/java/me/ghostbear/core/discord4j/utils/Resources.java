package me.ghostbear.core.discord4j.utils;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

non-sealed public interface Resources extends DiscordInteractionPropertySupplier<String[]> {

	@Contract(pure = true)
	static @NotNull Resources of(String... values) {
		return () -> values;
	}

}
