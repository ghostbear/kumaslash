# KumaSlash

**Automatic**
- Send user the reason why they were timed out

**Slash Commands**
- `/ping`
- `/source` `id:`
- `/download` `repository:`
- `/user`
  - `avatar` `target:`
  - `banner` `target:`
- `/steps`
- `/jumbo` `emote:`
- `/github`
  - `issue` `repository:` `number:`
  - `pull-request` `repository:` `number:`

**Message Commands**
- `Request Steps`

**Scopes**
- `application.commands`
- `bot`

**Bot Permissions**
- `View Audit Log`
- `Send Messages`
- `View Channels`
- `View Message History`
- `Attach Files`

**Privileged Gateway Intents**
- `SERVER MEMBERS INTENT`

[Permission calculator](https://discordapi.com/permissions.html#101376)

## Development
- 100% Kotlin
- Uses [version catalog](https://docs.gradle.org/current/userguide/platforms.html) for dependency management
- Use [ktlint](https://github.com/pinterest/ktlint) to check code style

## Running with Docker

### GitHub Release
```shell
docker run  \
  --env BOT_TOKEN=<YOUR_TOKEN> \
  -d ghcr.io/ghostbear/kumaslash:latest
```

### Building
```shell
docker build -t kumaslash:latest .
docker run  \
  --env BOT_TOKEN=<YOUR_TOKEN> \
  -d kumaslash:latest
```

## Developing

If you choose to develop a feature or fix a bug it's highly recommended that you use Intellij IDEA Community Edition as your code editor. 

### Running

1. (Optional) Set System Environment variables for `KUMASLASH_GUILD_ID` and `KUMASLASH_DEBUG`
   - `KUMASLASH_GUILD_ID` is the ID of your Discord Server
   - `KUMASLASH_DEBUG` should be set to `true` when developing with the bot
2. Build the JAR 
   - `./gradlew installDist`
3. Run the JAR
   - `./app/build/install/app/bin/app <BOT_TOKEN> <IGNORE_ROLES>`

### Intellij IDEA

1. (Optional) Set System Environment variables for `KUMASLASH_GUILD_ID` and `KUMASLASH_DEBUG`
    - `KUMASLASH_GUILD_ID` is the ID of your Discord Server
    - `KUMASLASH_DEBUG` should be set to `true` when developing with the bot
2. Open Run/Debug Configuration `Run -> Edit Configurations...`
3. Click on the Run template for your operative system
4. Duplicate the Run template by either pressing Copy Configuration or press `CTRL + D`
5. Add Script options to your `BOT_TOKEN` and/or `<IGNORE_ROLES>`
6. Press `Apply` and `OK`

Now select the newly created Run/Debug Configuration profile from the dropdown and press the Run button

![img.png](.github/assets/run_profiles.png)