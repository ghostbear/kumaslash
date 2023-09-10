package me.ghostbear.kumaslash.tachiyomi.local;

import me.ghostbear.kumaslash.tachiyomi.local.model.Source;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface SourceRepository extends ReactiveCrudRepository<Source, String> {

	@Query("SELECT T.id FROM (SELECT source.id, SIMILARITY(:id, id) AS s FROM source) AS T WHERE T.s > 0.33 ORDER BY s DESC LIMIT 1")
	Mono<String> fuzzySearch(String id);

}
