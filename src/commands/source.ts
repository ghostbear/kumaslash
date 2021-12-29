import { CommandInteraction, MessageActionRow, MessageButton } from "discord.js"
import { Discord, Slash, SlashOption } from "discordx"
import { GitHubRepository } from "../data/github-repository"

@Discord()
abstract class SourceCommand {

	repository = new GitHubRepository()

  @Slash("source")
	async hello(
    @SlashOption("id", { type: "STRING" })
    	id: string,
    	interaction: CommandInteraction
	) {
		const extension = await this.repository.findExtension(id)

		if (extension != null) {
			const download = 
				new MessageActionRow()
					.addComponents(
						new MessageButton()
							.setLabel("Download extension")
							.setStyle("LINK")
							.setURL(this.repository.getDownloadUrl(extension)),
						new MessageButton()
							.setLabel(`Version ${extension.version}`)
							.setCustomId("version")
							.setStyle("SECONDARY")
							.setDisabled(true)
					)
			interaction.reply({
				content: `<:nadeshikosuccess:843281745088217158> Source was found in the **${extension.name.replace("Tachiyomi: ", "")}** extension.`,
				components: [download],
				ephemeral: true
			})
		} else {
			interaction.reply({
				content: "Couldn't find any source with that ID.\nIf you entered the right ID, the source might have been removed.",
				ephemeral: true
			})
		}
	}
}