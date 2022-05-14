# KumaSlash

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
- `Send Messages`
- `View Channels`
- `View Message History`
- `Attach Files`

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

### Additional options

#### Ignore roles

You can add roles that the bot will ignore by adding `--env IGNORE_ROLES=<IGNORE_ROLES>`, `<IGNORE_ROLES>` is a string where you add the role ids you want to ignore.

**Example**

`123456789` will add ignore the role `123456789`

You can also ignore multiple roles by adding `,` between them, like this `123456789,987654321` this will ignore the roles `123456789` and `987654321`

```shell
docker run  \
  --env BOT_TOKEN=<YOUR_TOKEN> \
  --env IGNORE_ROLES=<IGNORE_ROLES> \
  -d ghcr.io/ghostbear/kumaslash:latest
  
```

## Developing

If you choose to develop a feature or fix a bug it's highly recommended that you use Intellij IDEA Community Edition as your code editor. 

### Running
1. Build the JAR 
   - `./gradlew installDist`
2. Run the JAR
   - `./app/build/install/app/bin/app <BOT_TOKEN> <IGNORE_ROLES>`

### Intellij IDEA
1. Open Run/Debug Configuration `Run -> Edit Configurations...`
2. Click on the Run template for your operative system
3. Duplicate the Run template by either pressing Copy Configuration or press `CTRL + D`
4. Add Script options to your `BOT_TOKEN` and/or `<IGNORE_ROLES>`
6. Press `Apply` and `OK`

Now select the newly created Run/Debug Configuration profile from the dropdown and press the Run button

![img.png](.github/assets/run_profiles.png)