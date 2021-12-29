import { ButtonInteraction, CommandInteraction, InteractionReplyOptions, MessageActionRow, MessageButton, MessagePayload } from "discord.js"
import { ButtonComponent, Discord, Slash, SlashChoice, SlashOption } from "discordx"
import { GitHubRepository } from "../data/githubrepository"
import { Assets, Release } from "../models"

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
      const message = this.createReleaseMessage(
        release.name,
        "📦",
        assest!
      )
      interaction.editReply(message)
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
  	const message = this.createReleaseMessage(
  		release.name,
  		"📦",
  		assest!
  	)
  	interaction.reply(message)
  }

  @ButtonComponent("tachiyomi-preview")
  async tachiyomiPreview(
  	interaction: ButtonInteraction
  ) {
  	const release = await this.repository.getRelease("tachiyomi", true)
  	const assest = release.assets.find((value) => /^tachiyomi-r\d{4,}.apk/.test(value.name))
  	const message = this.createReleaseMessage(
  		release.name,
  		"🔥",
  		assest!
  	)
  	interaction.reply(message)
  }

  @ButtonComponent("tachiyomi-sy-stable")
  async tachiyomiSyStable(interaction: ButtonInteraction) {
  	const release = await this.repository.getRelease("tachiyomi-sy", false)
  	const assest = release.assets.find((value) => value.name.includes(".apk"))
    const message = this.createReleaseMessage(
  		release.name,
  		"📦",
  		assest!
  	)
  	interaction.reply(message)
  }

  @ButtonComponent("tachiyomi-sy-preview")
  async tachiyomiSyPreview(
  	interaction: ButtonInteraction
  ) {
  	const release = await this.repository.getRelease("tachiyomi-sy", true)
  	const assest = release.assets.find((value) => value.name.includes(".apk"))
  	const message = this.createReleaseMessage(
  		release.name,
  		"🔥",
  		assest!
  	)
  	interaction.reply(message)
  }

  createReleaseMessage(
  	message: string, 
  	emoji: string, 
  	assets: Assets
  ): string | InteractionReplyOptions | MessagePayload {
  	return {
  		content: message,
  		components: [
  			new MessageActionRow()
  				.addComponents(
  					new MessageButton()
  						.setLabel(assets.name)
  						.setStyle("LINK")
  						.setEmoji(emoji)
  						.setURL(assets.browser_download_url)
  				)	
  		],
  		ephemeral: true
  	}
  }
}