package me.ghostbear.core.discord4j;

import discord4j.common.util.Snowflake;
import discord4j.discordjson.json.ApplicationCommandOptionData;
import discord4j.discordjson.json.ApplicationCommandRequest;
import discord4j.discordjson.json.ImmutableApplicationCommandRequest;
import discord4j.discordjson.possible.Possible;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public record DiscordApplicationCommandRequest(
		Optional<Snowflake> guildId,
		String name,
		Possible<Optional<Map<String, String>>> nameLocalizations,
		Possible<String> description,
		Possible<Optional<Map<String, String>>> descriptionLocalizations,
		Possible<List<ApplicationCommandOptionData>> options,
		Possible<Boolean> dmPermission,
		Possible<Boolean> defaultPermission,
		Optional<String> defaultMemberPermissions,
		Possible<Integer> type
) implements ApplicationCommandRequest {

	public DiscordApplicationCommandRequest {
		guildId = Objects.requireNonNullElseGet(guildId, Optional::empty);
		name = Objects.requireNonNull(name);
		nameLocalizations = Objects.requireNonNullElseGet(nameLocalizations, Possible::absent);
		description = Objects.requireNonNullElseGet(description, Possible::absent);
		descriptionLocalizations = Objects.requireNonNullElseGet(descriptionLocalizations, Possible::absent);
		options = Objects.requireNonNullElseGet(options, Possible::absent);
		dmPermission = Objects.requireNonNullElseGet(dmPermission, Possible::absent);
		defaultPermission = Objects.requireNonNullElseGet(defaultPermission, Possible::absent);
		defaultMemberPermissions = Objects.requireNonNullElseGet(defaultMemberPermissions, Optional::empty);
		type = Objects.requireNonNullElseGet(type, Possible::absent);
	}

	public DiscordApplicationCommandRequest(ApplicationCommandRequest applicationCommandRequest) {
		this(applicationCommandRequest instanceof DiscordApplicationCommandRequest r ? r.guildId() : Optional.empty(), applicationCommandRequest.name(), applicationCommandRequest.nameLocalizations(), applicationCommandRequest.description(), applicationCommandRequest.descriptionLocalizations(), applicationCommandRequest.options(), applicationCommandRequest.dmPermission(), applicationCommandRequest.defaultPermission(), applicationCommandRequest.defaultMemberPermissions(), applicationCommandRequest.type());
	}

	public static DiscordApplicationCommandRequest withName(String name) {
		return new DiscordApplicationCommandRequest(Optional.empty(), name, Possible.absent(), Possible.absent(), Possible.absent(), Possible.absent(), Possible.absent(), Possible.absent(), Optional.empty(), Possible.absent());
	}

	public DiscordApplicationCommandRequest withGuildId(Snowflake guildId) {
		return new DiscordApplicationCommandRequest(Optional.of(guildId), name, nameLocalizations, description, descriptionLocalizations, options, dmPermission, defaultPermission, defaultMemberPermissions, type);
	}

	public DiscordApplicationCommandRequest withNameLocalizations(Map<String, String> nameLocalizations) {
		return new DiscordApplicationCommandRequest(guildId, name, Possible.of(Optional.of(nameLocalizations)), description, descriptionLocalizations, options, dmPermission, defaultPermission, defaultMemberPermissions, type);
	}

	public DiscordApplicationCommandRequest withDescription(String description) {
		return new DiscordApplicationCommandRequest(guildId, name, nameLocalizations, Possible.of(description), descriptionLocalizations, options, dmPermission, defaultPermission, defaultMemberPermissions, type);
	}

	public DiscordApplicationCommandRequest withDescriptionLocalizations(Map<String, String> descriptionLocalizations) {
		return new DiscordApplicationCommandRequest(guildId, name, nameLocalizations, description, Possible.of(Optional.of(descriptionLocalizations)), options, dmPermission, defaultPermission, defaultMemberPermissions, type);
	}
	public DiscordApplicationCommandRequest withOptions(List<ApplicationCommandOptionData> options) {
		return new DiscordApplicationCommandRequest(guildId, name, nameLocalizations, description, descriptionLocalizations, Possible.of(options), dmPermission, defaultPermission, defaultMemberPermissions, type);
	}

	public DiscordApplicationCommandRequest withDmPermission(boolean dmPermission) {
		return new DiscordApplicationCommandRequest(guildId, name, nameLocalizations, description, descriptionLocalizations, options, Possible.of(dmPermission), defaultPermission, defaultMemberPermissions, type);
	}

	public DiscordApplicationCommandRequest withDefaultPermission(boolean defaultPermission) {
		return new DiscordApplicationCommandRequest(guildId, name, nameLocalizations, description, descriptionLocalizations, options, dmPermission, Possible.of(defaultPermission), defaultMemberPermissions, type);
	}

	public DiscordApplicationCommandRequest withDefaultMemberPermissions(String defaultMemberPermissions) {
		return new DiscordApplicationCommandRequest(guildId, name, nameLocalizations, description, descriptionLocalizations, options, dmPermission, defaultPermission, Optional.of(defaultMemberPermissions), type);
	}

	public DiscordApplicationCommandRequest withType(int type) {
		return new DiscordApplicationCommandRequest(guildId, name, nameLocalizations, description, descriptionLocalizations, options, dmPermission, defaultPermission, defaultMemberPermissions, Possible.of(type));
	}

	public ApplicationCommandRequest asImmutable() {
		return ImmutableApplicationCommandRequest.copyOf(this);
	}

}
