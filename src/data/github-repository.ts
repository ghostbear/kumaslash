import axios, { AxiosResponse } from "axios"
import { Extension, Release } from "../models"

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

	async getRelease(type: string, preview: boolean): Promise<Release> {
		let data!: Promise<AxiosResponse<any, any>>
		switch (type) {
		case "tachiyomi":
			if (preview) {
				data = axios.get("https://api.github.com/repos/tachiyomiorg/tachiyomi-preview/releases/latest")
			} else {
				data = axios.get("https://api.github.com/repos/tachiyomiorg/tachiyomi/releases/latest")
			}
			break
		case "tachiyomi-sy":
			if (preview) {
				data = axios.get("https://api.github.com/repos/jobobby04/TachiyomiSYPreview/releases/latest")
			} else {
				data = axios.get("https://api.github.com/repos/jobobby04/TachiyomiSY/releases/latest")
			}
			break
		case "tachiyomi-j2k":
			data = axios.get("https://api.github.com/repos/Jays2Kings/tachiyomiJ2K/releases/latest")
			break
		case "neko":
			data = axios.get("https://api.github.com/repos/CarlosEsco/Neko/releases/latest")
			break
		default: 
			throw "This shouldn't happend"
		}
		
		return (await data).data
	}
}