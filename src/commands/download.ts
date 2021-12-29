import {
	ButtonInteraction,
	CommandInteraction,
	MessageActionRow,
	MessageButton,
} from "discord.js"
import {
	ButtonComponent,
	Discord,
	Slash,
	SlashChoice,
	SlashOption,
} from "discordx"
import { ARelease, Neko, Tachiyomi, TachiyomiJ2K, TachiyomiSy } from "../data/release-repository"
import { toTitleCase } from "../utils/string"

@Discord()
abstract class DownloadCommand {
	  
	release = new Map<string, ARelease>()

	constructor() {
		this.release.set("tachiyomi", new Tachiyomi())
		this.release.set("tachiyomi-sy", new TachiyomiSy())
		this.release.set("tachiyomi-j2k", new TachiyomiJ2K())
		this.release.set("neko", new Neko())
	}

  @Slash("download")
	async hello(
    @SlashChoice("Neko", "neko")
    @SlashChoice("Tachiyomi J2K", "tachiyomi-j2k")
    @SlashChoice("Tachiyomi Sy", "tachiyomi-sy")
    @SlashChoice("Tachiyomi", "tachiyomi")
    @SlashOption("type", {
    	description: "Which version do you want to download",
    })
    	type: string,
    	interaction: CommandInteraction
	) {
		await interaction.deferReply({ ephemeral: true })

		const onlyStableReleases = ["tachiyomi-j2k", "neko"]
		if (onlyStableReleases.includes(type)) {
			const message = await this.release.get(type)!.createMesseage()
			interaction.editReply(message)
			return
		}

		interaction.editReply({
			content: `Which version of ${toTitleCase(type.replace("-", " "))}?`,
			components: [
				new MessageActionRow()
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
			],
		})
	}

  @ButtonComponent("tachiyomi-stable")
  async tachiyomiStable(interaction: ButtonInteraction) {
  	const message = await this.release.get("tachiyomi")!.createMesseage()
  	interaction.reply(message)
  }

  @ButtonComponent("tachiyomi-preview")
  async tachiyomiPreview(interaction: ButtonInteraction) {
  	const message = await this.release.get("tachiyomi")!.createMesseage({ preview: true })
  	interaction.reply(message)
  }

  @ButtonComponent("tachiyomi-sy-stable")
  async tachiyomiSyStable(interaction: ButtonInteraction) {
  	const message = await this.release.get("tachiyomi-sy")!.createMesseage()
  	interaction.reply(message)
  }

  @ButtonComponent("tachiyomi-sy-preview")
  async tachiyomiSyPreview(interaction: ButtonInteraction) {
  	const message = await this.release.get("tachiyomi-sy")!.createMesseage({ preview: true })
  	interaction.reply(message)
  }
}
