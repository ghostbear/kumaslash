import { ButtonInteraction, CommandInteraction, MessageActionRow, MessageButton } from "discord.js"
import { ButtonComponent, Discord, Slash, SlashChoice, SlashOption } from "discordx"
import { GitHubRepository } from "../data/githubrepository"

@Discord()
abstract class DownloadCommand {

	repository = new GitHubRepository()

  @Slash("download")
	async hello(
        @SlashChoice("Neko", "neko")
        @SlashChoice("Tachiyomi J2K", "tachiyomi-j2k")
        @SlashChoice("Tachiyomi Sy", "tachiyomi-sy")
        @SlashChoice("Tachiyomi", "tachiyomi")
        @SlashOption("type", { description: "Which version do you want to download" })
    	type: string,
    	interaction: CommandInteraction
	) {
  	await interaction.deferReply({ ephemeral: true })
    
		const onlyStableReleases = ["tachiyomi-j2k", "neko"]
		if (onlyStableReleases.includes(type)) {
			const release = await this.repository.getRelease(type, false)
			const assest = release.assets.find((value) => value.name.includes(".apk"))
			interaction.editReply({
				content: `${release.name}`,
				components: [
					new MessageActionRow()
						.addComponents(
							new MessageButton()
								.setLabel(assest?.name ?? "")
								.setStyle("LINK")
								.setEmoji("📦")
								.setURL(assest?.browser_download_url ?? "")
						)	
				]
			})
			return
		}

  	const row = new MessageActionRow()
			.addComponents(
				new MessageButton()
					.setLabel("Stable")
					.setStyle("PRIMARY")
					.setEmoji("📦")
					.setCustomId(`${type}-stable`)
			)	
			.addComponents(
				new MessageButton()
					.setLabel("Preview")
					.setStyle("PRIMARY")
					.setEmoji("🔥")
					.setCustomId(`${type}-preview`)
			)
    		
  	interaction.editReply({
  		content: "Which version?",
  		components: [row],
  	})
	}

  @ButtonComponent("tachiyomi-stable")
  async tachiyomiStable(interaction: ButtonInteraction) {
  	const release = await this.repository.getRelease("tachiyomi", false)
  	const assest = release.assets.find((value) => /^tachiyomi-v\d+\.\d+\.\d+.apk/.test(value.name))
  	interaction.reply({
  		content: `${release.name}`,
  		components: [
  			new MessageActionRow()
  				.addComponents(
  					new MessageButton()
  						.setLabel(assest?.name ?? "")
  						.setStyle("LINK")
  						.setEmoji("📦")
  						.setURL(assest?.browser_download_url ?? "")
  				)	
  		],
  		ephemeral: true
  	})
  }

  @ButtonComponent("tachiyomi-preview")
  async tachiyomiPreview(
  	interaction: ButtonInteraction
  ) {
  	const release = await this.repository.getRelease("tachiyomi", true)
  	const assest = release.assets.find((value) => /^tachiyomi-r\d{4,}.apk/.test(value.name))
  	interaction.reply({
  		content: `${release.name}`,
  		components: [
  			new MessageActionRow()
  				.addComponents(
  					new MessageButton()
  						.setLabel(assest?.name ?? "")
  						.setStyle("LINK")
  						.setEmoji("🔥")
  						.setURL(assest?.browser_download_url ?? "")
  				)	
  		],
  		ephemeral: true
  	})
  }

  @ButtonComponent("tachiyomi-sy-stable")
  async tachiyomiSyStable(interaction: ButtonInteraction) {
  	const release = await this.repository.getRelease("tachiyomi-sy", false)
  	const assest = release.assets.find((value) => value.name.includes(".apk"))
  	interaction.reply({
  		content: `${release.name}`,
  		components: [
  			new MessageActionRow()
  				.addComponents(
  					new MessageButton()
  						.setLabel(assest?.name ?? "")
  						.setStyle("LINK")
  						.setEmoji("📦")
  						.setURL(assest?.browser_download_url ?? "")
  				)	
  		],
  		ephemeral: true
  	})
  }

  @ButtonComponent("tachiyomi-sy-preview")
  async tachiyomiSyPreview(
  	interaction: ButtonInteraction
  ) {
  	const release = await this.repository.getRelease("tachiyomi-sy", true)
  	const assest = release.assets.find((value) => value.name.includes(".apk"))
  	interaction.reply({
  		content: `${release.name}`,
  		components: [
  			new MessageActionRow()
  				.addComponents(
  					new MessageButton()
  						.setLabel(assest?.name ?? "")
  						.setStyle("LINK")
  						.setEmoji("🔥")
  						.setURL(assest?.browser_download_url ?? "")
  				)	
  		],
  		ephemeral: true
  	})
  }
}