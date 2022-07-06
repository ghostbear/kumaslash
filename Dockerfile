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

FROM adoptopenjdk/openjdk16:alpine-jre AS bot
WORKDIR /kumaslash-bot
COPY --from=builder /build/app/build/install/app/ .
CMD ./bin/app $(echo $BOT_TOKEN) $(echo $IGNORE_ROLES)

FROM adoptopenjdk/openjdk16:alpine-jre AS backend
WORKDIR /kumaslash-backend
COPY --from=builder /build/web/build/install/web/ .
CMD ./bin/web $(echo $BOT_TOKEN) $(echo $IGNORE_ROLES)
