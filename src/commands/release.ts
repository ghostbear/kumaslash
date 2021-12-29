import {
	ButtonInteraction,
	CommandInteraction,
	MessageActionRow,
	MessageButton,
} from "discord.js"
import {
	ButtonComponent,
	Discord,
	Permission,
	Slash,
	SlashChoice,
	SlashOption,
} from "discordx"
import { Flavour, Neko, Tachiyomi, TachiyomiJ2K, TachiyomiSy } from "../data/flavour-repository"
import { toTitleCase } from "../utils/string"
import dotenv from "dotenv"
dotenv.config()

@Discord()
abstract class ReleaseCommand {
	  
	flavours = new Map<string, Flavour>()

	constructor() {
		this.flavours.set("tachiyomi", new Tachiyomi())
		this.flavours.set("tachiyomi-sy", new TachiyomiSy())
		this.flavours.set("tachiyomi-j2k", new TachiyomiJ2K())
		this.flavours.set("neko", new Neko())
	}

  @Slash("download")
  async download(
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
  		const message = await this.flavours.get(type)!.createMesseage()
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
  	const message = await this.flavours.get("tachiyomi")!.createMesseage()
  	interaction.reply(message)
  }

  @ButtonComponent("tachiyomi-preview")
  async tachiyomiPreview(interaction: ButtonInteraction) {
  	const message = await this.flavours.get("tachiyomi")!.createMesseage({ preview: true })
  	interaction.reply(message)
  }

  @ButtonComponent("tachiyomi-sy-stable")
  async tachiyomiSyStable(interaction: ButtonInteraction) {
  	const message = await this.flavours.get("tachiyomi-sy")!.createMesseage()
  	interaction.reply(message)
  }

  @ButtonComponent("tachiyomi-sy-preview")
  async tachiyomiSyPreview(interaction: ButtonInteraction) {
  	const message = await this.flavours.get("tachiyomi-sy")!.createMesseage({ preview: true })
  	interaction.reply(message)
  }
}
