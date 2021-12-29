import { InteractionReplyOptions, MessageActionRow, MessageButton, MessagePayload } from "discord.js"
import { HOUR_IN_MILLIS } from "../constants"
import { Assets, Release } from "../models"
import { GitHubRepository } from "./github-repository"

type Message = string | InteractionReplyOptions | MessagePayload;

export abstract class Flavour {

  abstract name: string

  isPreviewSupported: boolean = false

  lastUpdated: [number, number] = [0, 0]
  
  release: [Release | undefined, Release | undefined] = [undefined, undefined]

  repository: GitHubRepository = new GitHubRepository()

  async getRelease(options?: { preview?: true }): Promise<Release> {
  	const isPreview = options?.preview ?? false
  	const now = Date.now()
  	if (!isPreview && this.lastUpdated[0] + HOUR_IN_MILLIS < now) {
  		this.release[0] = await this.repository.getRelease(this.name, false)
  	}
  	if (isPreview && this.isPreviewSupported && this.lastUpdated[1] + HOUR_IN_MILLIS < now) {
  		this.release[1] = await this.repository.getRelease(this.name, true)
  	}
  	return isPreview ? this.release[1]! : this.release[0]!
  }

  generateMessageActionRow(isPreview: boolean, release: Release, assets: Assets): MessageActionRow {
  	return new MessageActionRow()
  		.addComponents(
  			new MessageButton()
  				.setLabel(assets?.name ?? "")
  				.setStyle("LINK")
  				.setEmoji(isPreview ? "🔥" : "📦")
  				.setURL(assets?.browser_download_url ?? "")
  		)
  		.addComponents(
  			new MessageButton()
  				.setLabel("Changelog")
  				.setStyle("LINK")
  				.setEmoji("📰")
  				.setURL(release.html_url)
  		)
  }

  generateContent(options: {
    title?: string,
    isPreview?: boolean
    warning?: string
    body?: string
  }): string {
  	let text = options.title ?? ""

  	if (options.isPreview) {
  		const warning = options.warning ?? "⚠ Preview is not recommended if you're not willing to test for – and endure – issues. ⚠"
  		text = warning + "\n\n" + text
  	}

  	if (options.body) {
  		text = text + "\n\n" + options.body
  	}

  	return text
  }

  abstract createMesseage(options?: { preview?: true }): Promise<Message> 

}

export class Tachiyomi extends Flavour {
	
	name: string = "tachiyomi"

	isPreviewSupported: boolean = true

	async createMesseage(options?: { preview?: true }): Promise<Message> {
		const isPreview = options?.preview ?? false
		const release = await this.getRelease(options)
		const regex = isPreview ? /^tachiyomi-r\d{4,}.apk/ : /^tachiyomi-v\d+\.\d+\.\d+.apk/
		const assets = release.assets.find((value) => regex.test(value.name))!
		return {
			content: this.generateContent({
				title: release.name,
				isPreview: isPreview
			}),
			components: [
				this.generateMessageActionRow(isPreview, release, assets)
			],
			ephemeral: true
		}
	}

}

export class TachiyomiSy extends Flavour {
	name: string = "tachiyomi-sy"

	isPreviewSupported: boolean = true

	async createMesseage(options?: { preview?: true | undefined; }): Promise<Message> {
		const isPreview = options?.preview ?? false
		const release = await this.getRelease(options)
		const assets = release.assets.find((value) => value.name.includes(".apk"))!
		return {
			content: this.generateContent({
				title: release.name,
				isPreview: isPreview
			}),
			components: [
				this.generateMessageActionRow(isPreview, release, assets)
			],
			ephemeral: true
		}
	}
}

export class TachiyomiJ2K extends Flavour {
	name: string = "tachiyomi-j2k"

	async createMesseage(options?: { preview?: true | undefined; }): Promise<Message> {
		const release = await this.getRelease()
		const assets = release.assets.find((value) => value.name.includes(".apk"))!
		return {
			content: this.generateContent({
				title: release.name,
			}),
			components: [
				this.generateMessageActionRow(false, release, assets)
			],
			ephemeral: true
		}
	}
}

export class Neko extends Flavour {
	name: string = "neko"

	async createMesseage(options?: { preview?: true | undefined; }): Promise<Message> {
		const release = await this.getRelease(options)
		const assets = release.assets.find((value) => value.name.includes(".apk"))!
		return {
			content: this.generateContent({
				title: "Neko " + release.name,
			}),
			components: [
				this.generateMessageActionRow(false, release, assets)
			],
			ephemeral: true
		}
	}
}