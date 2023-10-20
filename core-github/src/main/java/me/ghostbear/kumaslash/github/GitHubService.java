package me.ghostbear.kumaslash.github;

import me.ghostbear.kumaslash.github.model.Issue;
import me.ghostbear.kumaslash.github.model.Release;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.GetExchange;
import reactor.core.publisher.Mono;

public interface GitHubService {

	@NotNull
	@GetExchange("/repos/{owner}/{repository}/issues/{issueNumber}")
	Mono<Issue> getIssue(@PathVariable("owner") String owner, @PathVariable("repository") String repository, @PathVariable("issueNumber") String issueNumber);

	@NotNull
	@GetExchange("/repos/{owner}/{repository}/releases/latest")
	Mono<Release> getLatestRelease(@PathVariable("owner") String owner, @PathVariable("repository") String repository);
}