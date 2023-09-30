package me.ghostbear.kumaslash.tachiyomi;

import me.ghostbear.kumaslash.github.GitHubService;
import me.ghostbear.kumaslash.github.model.Release;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class TachiyomiFlavourService {

	private final GitHubService gitHubService;

	public TachiyomiFlavourService(GitHubService gitHubService) {
		this.gitHubService = gitHubService;
	}

	public Mono<Release> getLatestRelease(String owner, String repository) {
		return gitHubService.getLatestRelease(owner, repository);
	}

}
