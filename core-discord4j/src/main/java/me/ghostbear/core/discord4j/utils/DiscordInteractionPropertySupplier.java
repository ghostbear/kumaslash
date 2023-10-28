package me.ghostbear.core.discord4j.utils;

import java.util.function.Supplier;

public sealed interface DiscordInteractionPropertySupplier<T> extends Supplier<T> permits Raw, Resources {
}
