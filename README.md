# KumaSlash

**Commands**
- `/source id:`
- `/release type:`

## Running

1. Build the JAR 
   - `./gradlew jar`
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