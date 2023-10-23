package me.ghostbear.core.discord4j;

import java.util.function.Supplier;

public sealed interface DiscordInteractionPropertySupplier<T> extends Supplier<T> permits Raw, Resources {
}
