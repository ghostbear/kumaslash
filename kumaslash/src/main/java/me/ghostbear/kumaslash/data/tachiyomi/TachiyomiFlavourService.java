package me.ghostbear.kumaslash.data.tachiyomi;

import me.ghostbear.kumaslash.data.github.GitHubWebClient;
import me.ghostbear.kumaslash.data.github.Release;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class TachiyomiFlavourService {

	private final GitHubWebClient gitHubWebClient;

	@Autowired
	public TachiyomiFlavourService(GitHubWebClient gitHubWebClient) {
		this.gitHubWebClient = gitHubWebClient;
	}

	public Mono<Release> getLatestRelease(String owner, String repository) {
		return gitHubWebClient.getLatestRelease(owner, repository);
	}

}
