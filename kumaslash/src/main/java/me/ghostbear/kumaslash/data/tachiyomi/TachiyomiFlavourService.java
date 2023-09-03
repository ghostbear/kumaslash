package me.ghostbear.kumaslash.data.tachiyomi;

import me.ghostbear.kumaslash.github.model.Release;
import me.ghostbear.kumaslash.github.GitHubRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class TachiyomiFlavourService {

	private final GitHubRepository gitHubRepository;

	@Autowired
	public TachiyomiFlavourService(GitHubRepository gitHubRepository) {
		this.gitHubRepository = gitHubRepository;
	}

	public Mono<Release> getLatestRelease(String owner, String repository) {
		return gitHubRepository.getLatestRelease(owner, repository);
	}

}
