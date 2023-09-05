package me.ghostbear.kumaslash.tachiyomi.local;

import me.ghostbear.kumaslash.tachiyomi.local.model.Extension;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface ExtensionRepository extends ReactiveCrudRepository<Extension, String> {
}
