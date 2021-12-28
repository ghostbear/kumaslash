import DiscordJS, { Intents, MessageActionRow, MessageButton } from 'discord.js'
import axios from 'axios'
import dotenv from 'dotenv'
dotenv.config()

interface Extension {
    name: string,
    pkg: string,
    apk: string,
    lang: string,
    code: number,
    version: string,
    nsfw: number,
    sources: Array<Source>
}

interface Source {
    name: string,
    lang: string,
    id: string,
    baseUrl: string
}

const HOUR_IN_MILLIS = 60 * 60 * 1000
let lastUpdated = 0

const extensionsUrl = 'https://raw.githubusercontent.com/tachiyomiorg/tachiyomi-extensions/repo/index.json'
const extensionsDownloadUrl = 'https://raw.githubusercontent.com/tachiyomiorg/tachiyomi-extensions/repo/apk/'
let extensions: Array<Extension> | null = null

function fetchExtensions() {
    extensions = null
    console.log('Updating extension list')
    axios.get(extensionsUrl)
        .then((response) => {
            
            lastUpdated = Date.now()
            extensions = response.data
        })
}

fetchExtensions()

const client = new DiscordJS.Client({
    intents: [
        Intents.FLAGS.GUILDS, 
        Intents.FLAGS.GUILD_MESSAGES
    ]
})

client.on('ready', () => {
    console.log('The bot is ready')

    const guildId = process.env.GUILD_ID ?? ''
    const guild = client.guilds.cache.get(guildId)
    let commands

    if (guild) {
        commands = guild.commands
    } else {
        commands = client.application?.commands
    }

    commands?.create({
        name: 'source',
        description: 'Get information about a source from its ID',
        options: [
            {
                name: 'id',
                description: 'The ID you want to search for',
                required: true,
                type: DiscordJS.Constants.ApplicationCommandOptionTypes.STRING
            }
        ]
    })
})

client.on('interaction', (interaction) => {
    if (!interaction.isCommand()) {
        return
    }

    if (lastUpdated + HOUR_IN_MILLIS < Date.now()) {
        fetchExtensions() 
    }

    const { commandName, options } = interaction

    if (commandName === 'source') {
        const id = options.getString('id')!

        while (extensions == null) {
            console.log('Waiting for extension')
        }

        const extension = extensions.find((extension) => {
            return extension.sources.find((source) => {
                return source.id === id
            }) != null
        })

        if (extension != null) {
            const download = new MessageActionRow()
			.addComponents(
				new MessageButton()
					.setLabel(`Download extension`)
					.setStyle('LINK')
                    .setURL(extensionsDownloadUrl + extension.apk),
                new MessageButton()
					.setLabel(`Version ${extension.version}`)
                    .setCustomId('version')
					.setStyle('SECONDARY')
                    .setDisabled(true)
			);
            interaction.reply({
                content: `<:nadeshikosuccess:843281745088217158> Source was found in the **${extension.name.replace("Tachiyomi: ", "")}** extension.`,
                components: [download],
                ephemeral: true
            })
        } else {
            interaction.reply({
                content: `Couldn't find any source with that ID.\nIf you entered the right ID, the source might have been removed.`,
                ephemeral: true
            })
        }
    }
})

client.login(process.env.TOKEN)