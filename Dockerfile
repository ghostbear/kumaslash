FROM eclipse-temurin:17-jdk-jammy as base
WORKDIR /project
COPY . ./
RUN ./gradlew dependencies

FROM base AS development-bot
CMD ./gradlew app:installDist&&chmod +x ./app/build/install/app/bin/app&&./app/build/install/app/bin/app

FROM base AS development-web
CMD ./gradlew web:installDist&&chmod +x ./web/build/install/web/bin/web&&./web/build/install/web/bin/web

FROM base AS build
RUN ./gradlew installDist&&chmod +x ./web/build/install/web/bin/web&&chmod +x ./app/build/install/app/bin/app

FROM eclipse-temurin:17-jre-jammy AS production-bot
COPY --from=build /project/app/build/install/app ./app
WORKDIR /app
CMD ./bin/app

FROM eclipse-temurin:17-jre-jammy AS production-web
COPY --from=build /project/web/build/install/web ./app
WORKDIR /app
CMD ./bin/web
