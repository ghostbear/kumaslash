export interface Extension {
  name: string
  pkg: string
  apk: string
  lang: string
  code: number
  version: string
  nsfw: number
  sources: Array<Source>
}

export interface Source {
  name: string
  lang: string
  id: string
  baseUrl: string
}

export interface Repository {
  stable: Release
  preview: Release | undefined
}

export interface Release {
  html_url: string
  name: string
  assets: Assets[]
}

export interface Assets {
  name: string
  browser_download_url: string
}
