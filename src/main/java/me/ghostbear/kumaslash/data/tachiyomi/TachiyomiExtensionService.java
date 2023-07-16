package me.ghostbear.kumaslash.data.tachiyomi;

import me.ghostbear.kumaslash.data.tachiyomi.client.TachiyomiWebClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public class TachiyomiExtensionService {

	private static final Logger LOG = LoggerFactory.getLogger(TachiyomiExtensionService.class);

	private final ExtensionRepository extensionRepository;
	private final SourceRepository sourceRepository;
	private final TachiyomiWebClient tachiyomiWebClient;

	@Autowired
	public TachiyomiExtensionService(ExtensionRepository extensionRepository, SourceRepository sourceRepository, TachiyomiWebClient tachiyomiWebClient) {
		this.extensionRepository = extensionRepository;
		this.sourceRepository = sourceRepository;
		this.tachiyomiWebClient = tachiyomiWebClient;
	}

	public Mono<Void> update() {
		return tachiyomiWebClient.getExtensions()
				.flatMapIterable(new ExtensionMapper()::map)

				.flatMap(value -> extensionRepository.existsById(value.packageName())
						.flatMap(exists -> extensionRepository.save(value.withIsNew(!exists)))
						.doOnError(throwable -> LOG.error("Failed to update extension", throwable))
						.flatMapIterable(extension -> value.sources().stream().map(source -> source.withExtension(extension)).toList())
						.flatMap(source -> sourceRepository.existsById(source.id())
								.flatMap(exists -> sourceRepository.save(source.withIsNew(!exists)))
								.doOnError(throwable -> LOG.error("Failed to update source", throwable))))
				.doOnError(e -> LOG.error("Failed to update sources"))
				.doOnComplete(() -> LOG.info("Succeeded to update sources"))
				.then();
	}

	public Mono<Source> findById(String sourceId) {
		return sourceRepository.fuzzySearch(sourceId)
				.flatMap(id -> sourceRepository.findById(id))
				.flatMap(source -> extensionRepository.findById(source.packageName())
						.map(source::withExtension))
				.doOnError(e -> LOG.error("Failed to find source by id", e));
	}

	public Mono<Extension> findByPackageName(String packageName) {
		return extensionRepository.findById(packageName);
	}

}
