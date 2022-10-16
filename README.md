# KumaSlash

**Automatic**
- Send user the reason why they were timed out
  - And to moderation log if set

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
- `/rules` `target:` `rule:`
- `/logging`
  - `set` `type:` `channel:`
  - `get` `type:`
  - `delete` `type:`
- `/social` `activity:` `target:`
- `/bug` `type:` `application:`
- `/feature` `type:` `application:`

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

### Docker (Recommended)
1. Copy and rename `kumaslash.env.example` to `kumaslash.env` and `kumanounchi.env.example` to `kumanounchi.env`
2. Get a bot token from Discord Developers and set the `BOT_TOKEN` in `kumanounchi.env`
3. Now run `docker-compose -f .\docker-compose.dev.yml up --build`
   - If you have issues with `docker-compose` on Windows like `gradlew dependecies` not working change the line endings in `gradlew` to `LF`
4. After building the base image run `docker-compose -f .\docker-compose.dev.yml restart` to apply code changes
### Manually
1. Set all the environment variables
    - See [Environment variables](#environment-variables)
2. Run `gradlew run`
    - If you are on Windows use `gradlew.bat` and if you are on macOS or Linux use `gradlew`
    - If you want to run the bot and backend server separately use `app:run` and `web:run` instead of `run`
### IntelliJ
1. Find the `run` task under the Gradle tab
2. Right-click the `run` task and select `Modify Run Configuration`
3. Set all the environment variables
    - See [Environment variables](#environment-variables)
4. Click Apply and OK

## Environment variables
```env
BOT_TOKEN={YOUR_BOT_TOKEN}
SERVER_URL=http://127.0.0.1         # Optional
SERVER_PORT=8080                    # Optional
KUMASLASH_GUILD_ID={YOUR_GUILD_ID}  # Optional, don't use in a release environment
KUMASLASH_DEBUG=true                # Optional, don't use in a release environment
```
