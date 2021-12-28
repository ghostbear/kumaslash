export interface Extension {
    name: string,
    pkg: string,
    apk: string,
    lang: string,
    code: number,
    version: string,
    nsfw: number,
    sources: Array<Source>
}

export interface Source {
    name: string,
    lang: string,
    id: string,
    baseUrl: string
}

