FROM gradle:jdk17-alpine AS builder
COPY . /build
USER root
RUN mkdir /kumaslash
RUN chown -R gradle /build
RUN chown -R gradle /kumaslash
RUN chmod +x /build/gradlew
USER gradle
WORKDIR /build
RUN ./gradlew installDist

FROM adoptopenjdk/openjdk16:alpine-jre
WORKDIR /kumaslash
COPY --from=builder /build/app/build/install/app/ .
CMD ./bin/app $(echo $BOT_TOKEN) $(echo $IGNORE_ROLES)

