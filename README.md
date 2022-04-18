# KumaSlash

**Commands**
- `/ping`
- `/source id:`
- `/release type:`
- `/user`
  - `avatar target:`
  - `banner target:`
- `/steps`

**Scopes**
- application.commands
- bot

**Bot Permissions**
- Send Messages
- Read Messages/View Channels

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
1. Build the JAR 
   - `./gradlew build`
2. Run the JAR
   - `java -jar build/libs/kumaslash-*.jar <BOT_TOKEN>`

### Intellij IDEA
1. Open Run/Debug Configuration `Run -> Edit Configurations...`
2. Add new JAR Application `+ -> JAR Application`
3. (Optional) Set the name field to a name, like your bots name
4. Set the Path to JAR to the JAR file in the build folder `Folder -> Find the JAR`
5. Set Program variables to your `BOT_TOKEN`
6. Under Before launch add the build task from Gradle
   1. Press `+` then `Run Gradle task`
   2. Set the Gradle project to `kumaslash`
   3. Set the task to `build`
   4. Press `OK`
7. Press `Apply` and `OK`

Now select the newly created Run/Debug Configuration profile from the dropdown and press the Run button

![img.png](.github/assets/run_profiles.png)