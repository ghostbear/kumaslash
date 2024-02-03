FROM azul/zulu-openjdk-alpine:21-latest AS build
ENV DOCKER_PIPELINE=true
WORKDIR /output
COPY . ./
RUN \
    --mount=type=cache,target=/output/.gradle \
    ./gradlew kumaslash:bootJar

FROM azul/zulu-openjdk-alpine:21-jre-headless-latest AS kumaslash
WORKDIR /deploy
COPY --from=build /output/kumaslash/build/libs/*.jar kumaslash.jar
CMD ["java", "-jar", "kumaslash.jar"]
