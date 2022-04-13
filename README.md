# KumaSlash

**Commands**
- `/ping`
- `/source id:`
- `/release type:`

## Development
- 100% Kotlin
- Uses [version catalog](https://docs.gradle.org/current/userguide/platforms.html) for dependency management
- Use [ktlint](https://github.com/pinterest/ktlint) to check code style

## Running

1. Build the JAR 
   - `./gradlew build`
2. Run the JAR
   - `java -jar build/libs/kumaslash-*.jar <BOT_TOKEN>`

## Docker 

## GitHub Release
```shell
docker run  \
  --env BOT_TOKEN=<YOUR_TOKEN> \
  -d ghcr.io/ghostbear/kumaslash:latest
```

## Building
```shell
docker build -t kumaslash:latest .
docker run  \
  --env BOT_TOKEN=<YOUR_TOKEN> \
  -d kumaslash:latest
```