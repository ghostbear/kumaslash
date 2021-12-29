import "reflect-metadata"
import { Client } from "discordx"
import { Intents } from "discord.js"
import { importx } from "@discordx/importer"
import dotenv from "dotenv"
dotenv.config()

async function start() {
	const TOKEN = process.env.TOKEN
	if (TOKEN == undefined) {
		throw "TOKEN is missing from enviroment variables"
	}

	const GUILD_ID = process.env.GUILD_ID
	const client = new Client({
		botId: "kumaslash",
		intents: [Intents.FLAGS.GUILDS, Intents.FLAGS.GUILD_MESSAGES],
		botGuilds: GUILD_ID ? [GUILD_ID] : [],
	})

	client.once("ready", async () => {
		if (GUILD_ID){
		  await client.clearApplicationCommands(GUILD_ID)
		}
		await client.initApplicationCommands()
		await client.initApplicationPermissions()
	})
  
	client.on("interactionCreate", (interaction) => {
		console.log(">> Bot interaction create")
		client.executeInteraction(interaction)
	})

	await importx(__dirname + "/{events,commands}/**/*.{ts,js}")
	await client.login(TOKEN)
}

start()
