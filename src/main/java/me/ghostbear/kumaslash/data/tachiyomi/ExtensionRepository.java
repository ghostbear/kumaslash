package me.ghostbear.kumaslash.data.tachiyomi;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface ExtensionRepository extends ReactiveCrudRepository<Extension, String> {
}
