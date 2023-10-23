package me.ghostbear.core.discord4j;

non-sealed public interface Resources extends DiscordInteractionPropertySupplier<String[]> {

	static me.ghostbear.core.discord4j.Resources of(String... values) {
		return () -> values;
	}

}
