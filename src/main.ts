import "reflect-metadata"
import { Client } from "discordx"
import { Intents } from "discord.js"
import dotenv from "dotenv"
import { importx } from "@discordx/importer"
dotenv.config()

async function start() {
	const client = new Client({
		botId: "test",
		intents: [Intents.FLAGS.GUILDS, Intents.FLAGS.GUILD_MESSAGES],
		botGuilds: [process.env.GUILD_ID!],
	})

	client.once("ready", async () => {
		await client.clearApplicationCommands(process.env.GUILD_ID!)
		await client.initApplicationCommands()
		await client.initApplicationPermissions()
	})
  
	client.on("interactionCreate", (interaction) => {
		console.log(">> Bot interaction create")
		client.executeInteraction(interaction)
	})

	await importx(__dirname + "/{events,commands}/**/*.{ts,js}")
	await client.login(process.env.TOKEN ?? "")
}

start()
