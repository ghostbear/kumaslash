import axios from "axios"
import { Extension } from "../models"

const HOUR_IN_MILLIS = 60 * 60 * 1000

const EXTENSIONS_URL = "https://raw.githubusercontent.com/tachiyomiorg/tachiyomi-extensions/repo/index.json"
const EXTENSIONS_DOWNLOAD_URL = "https://raw.githubusercontent.com/tachiyomiorg/tachiyomi-extensions/repo/apk/"

export class GitHubRepository {
	lastUpdated: number = 0
	extensions: Extension[] = []

	constructor() {
		this.getExtensions()
	}

	async getExtensions(): Promise<Array<Extension>> {
		const now = Date.now()
		if (this.lastUpdated + HOUR_IN_MILLIS < now) {
			this.extensions = (await axios.get(EXTENSIONS_URL)).data
			this.lastUpdated = now
		}
		return this.extensions
	}

	async findExtension(id: string): Promise<Extension | undefined> {
		const extensions = await this.getExtensions()
		
		return extensions.find((extension) => {
			return extension.sources.find((source) => {
				return source.id === id
			})
		})
	}

	getDownloadUrl(extension: Extension): string {
		return EXTENSIONS_DOWNLOAD_URL + extension.apk
	}

}