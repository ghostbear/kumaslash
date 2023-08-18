FROM azul/zulu-openjdk-alpine:20.0.2 AS build
ENV DOCKER_PIPELINE=true
WORKDIR /output
COPY . ./
RUN ./gradlew bootJar

FROM azul/zulu-openjdk-alpine:20.0.2-jre AS kumaslash
WORKDIR /deploy
COPY --from=build /output/kumaslash/build/libs/kumaslash.jar .
CMD ["java", "-jar", "kumaslash.jar"]
