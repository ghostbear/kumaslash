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

FROM openjdk:17-jdk-alpine
WORKDIR /kumaslash
COPY --from=builder /build/app/build/install/app/ .
CMD ./bin/app $(echo $BOT_TOKEN) $(echo $IGNORE_ROLES)

