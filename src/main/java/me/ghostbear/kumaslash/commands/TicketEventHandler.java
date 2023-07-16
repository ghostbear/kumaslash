package me.ghostbear.kumaslash.commands;

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.command.ApplicationCommandInteractionOption;
import discord4j.core.object.command.ApplicationCommandInteractionOptionValue;
import discord4j.core.object.component.ActionRow;
import discord4j.core.object.component.Button;
import discord4j.core.spec.EmbedCreateFields;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.core.spec.InteractionFollowupCreateMono;
import discord4j.rest.util.Color;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import me.ghostbear.kumaslash.commands.core.SlashCommandEventHandler;
import me.ghostbear.kumaslash.configuration.TachiyomiProperties;
import me.ghostbear.kumaslash.data.github.GitHubWebClient;
import me.ghostbear.kumaslash.data.github.Issue;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class TicketEventHandler implements SlashCommandEventHandler.SlashCommand {

	private final List<TachiyomiProperties.Flavour> flavours;
	private final GitHubWebClient gitHubWebClient;

	@Autowired
	public TicketEventHandler(TachiyomiProperties tachiyomiProperties, GitHubWebClient gitHubWebClient) {
		this.flavours = tachiyomiProperties.getFlavours();
		this.gitHubWebClient = gitHubWebClient;
	}

	@Override
	public String getName() {
		return "ticket";
	}

	@Override
	public Mono<Void> handle(ChatInputInteractionEvent event) {
		var target = event.getOption("flavour")
				.flatMap(ApplicationCommandInteractionOption::getValue)
				.map(ApplicationCommandInteractionOptionValue::asString)
				.orElseThrow();
		var flavour = flavours.stream()
				.filter(value -> value.getRepository().equals(target))
				.findFirst()
				.orElseThrow();
		var issueNumber = event.getOption("number")
				.flatMap(ApplicationCommandInteractionOption::getValue)
				.map(ApplicationCommandInteractionOptionValue::asLong)
				.orElseThrow();
		return event.deferReply()
				.then(gitHubWebClient.getIssue(flavour.getOwner(), flavour.getRepository(), String.valueOf(issueNumber)))
				.flatMap(issue -> createFollowup(event, issue, flavour))
				.then();
	}

	public InteractionFollowupCreateMono createFollowup(ChatInputInteractionEvent event, Issue issue, TachiyomiProperties.Flavour flavour) {
		if (Objects.nonNull(issue.pullRequest())) {
			return createPullRequestFollowup(event, issue, flavour);
		}
		return createIssueFollowup(event, issue, flavour);
	}

	private InteractionFollowupCreateMono createIssueFollowup(ChatInputInteractionEvent event, Issue issue, TachiyomiProperties.Flavour flavour) {
		return event.createFollowup()
				.withEmbeds(EmbedCreateSpec.builder()
						.title(issue.title())
						.description(cleanDescription(issue.body()))
						.image("https://opengraph.githubassets.com/%s/%s/%s/issues/%s".formatted(UUID.randomUUID(), flavour.getOwner(), flavour.getRepository(), issue.number()))
						.footer(EmbedCreateFields.Footer.of("Issue by " + issue.user().login(), issue.user().avatarUrl()))
						.color(issue.state().equalsIgnoreCase("open") ? Color.GREEN : Color.RED)
						.build())
				.withComponents(ActionRow.of(Button.link(issue.htmlUrl(), "Open issue in browser")));
	}

	private InteractionFollowupCreateMono createPullRequestFollowup(ChatInputInteractionEvent event, Issue issue, TachiyomiProperties.Flavour flavour) {
		return event.createFollowup()
				.withEmbeds(EmbedCreateSpec.builder()
						.title(issue.title())
						.description(cleanDescription(issue.body()))
						.image("https://opengraph.githubassets.com/%s/%s/%s/pull/%s".formatted(UUID.randomUUID(), flavour.getOwner(), flavour.getRepository(), issue.number()))
						.footer(EmbedCreateFields.Footer.of("Pull Request by " + issue.user().login(), issue.user().avatarUrl()))
						.color(switch (issue.state().toLowerCase()) {
							case "open" -> issue.draft() ? Color.GRAY_CHATEAU : Color.GREEN;
							case "closed" -> Objects.nonNull(issue.pullRequest()) && issue.pullRequest().isMerged() ? Color.MAGENTA : Color.RED;
							default -> Color.BLACK;
						})
						.build())
				.withComponents(ActionRow.of(Button.link(issue.pullRequest().htmlUrl(), "Open pull request in browser")));
	}

	String cleanDescription(String description) {
        return StringUtils.substringBeforeLast(description, "### Acknowledgements")
				.replaceAll("(?:<!--)(.*?)(?:-->)", "")
				.replaceAll("^((?:#{2,})\s(?:.*))(\n\n)", "$1\n")
				.replaceAll("^(?:#{2,})\s(.*)$", "**$1**")
				.substring(0, 384);
	}

}
