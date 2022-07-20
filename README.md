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
- `/rules` `target:` `rule:`

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

1. Create a directory 
   - `mkdir kumaslash`
2. In the directory create a file called `docker-compose.yaml`
   - `touch docker-compose.yaml`
3. Copy the code either under **GitHub Release** or **Building** and paste it into the `docker-compose.yaml` file
4. Before running in the directory create an empty file called `kumaslash.db`
    - `touch kumaslash.db`
5. Run the file called `docker-compose.yaml`
   - `docker-compose up -d`

### GitHub Release
```dockerfile
version: "3.9"
services:
  backend:
    image: ghcr.io/ghostbear/kumaslash-backend:latest
    volumes:
      - type: bind
        source: ./kumaslash.db
        target: /kumaslash-backend/kumaslash.db
  bot:
    environment:
      - BOT_TOKEN={YOUR_BOT_TOKEN}
      - KUMASLASH_BACKEND_URL=backend # Should match the name of the backend service
      - KUMASLASH_BACKEND_PORT=8080 # Backend server uses 8080 by default
    image: ghcr.io/ghostbear/kumaslash-bot:latest
```

### Building
```dockerfile
version: "3.9"
services:
  backend:
    image: kumaslash-backend
    build:
      context: .
      target: backend
    volumes:
      - type: bind
        source: ./kumaslash.db
        target: /kumaslash-backend/kumaslash.db
  bot:
    environment:
      - BOT_TOKEN={YOUR_BOT_TOKEN}
      - KUMASLASH_BACKEND_URL=backend # Should match the name of the backend service
      - KUMASLASH_BACKEND_PORT=8080 # Backend server uses 8080 by default
    image: kumaslash-bot
    build:
      context: .
      target: bot
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