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

```shell
docker build .
docker run  \
  --env BOT_TOKEN=<YOUR_TOKEN> \
  -d <image_name>:latest
```