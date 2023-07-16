# KumaSlash

## Setup

### Requirements

- Docker
- A Network Connection

### Discord

To get started create a new application over at the [Discord Developer Portal](https://discord.com/developers/applications),
if you don't have one already. Then head over to the bot tab and give it the following permissions and privileged gateway intents.
You could also give it `Administator` for permission then you don't have to worry about permissions.

> You can use the [Discord Permission Calculator](https://discordapi.com/permissions.html) to create an invitation link for the bot with all permission you need.

#### Permissions
- View Audit Log
- Manage Server
- Moderate Members
- Send Messages
- Read Messages/View Channels

#### Discord Privileged Gateway Intents
- Server Members
- Message Content

### Running through Docker

Docker Images of KumaSlash is published to GitHub Registry when a new release is made and can be used to run KumaSlash
through Docker or Docker Compose.

<details>
  <summary>Docker Compose instructions</summary>

To set up KumaSlash through Docker Compose. Start with creating a Docker Compose file, with a name you remember like
`docker-compose.kumaslash.yml`. The **Adminer** service is totally optional but more or less all data is modifiable through
the database, there will most likely be slash commands or some-kind of interface to edit this data in the future.

```yml
version: '3'
services:
  kumaslash:
    image: ghcr.io/ghostbear/kumaslash:${VERSION}
    deploy:
      restart_policy:
        condition: on-failure
        delay: 3s
        max_attempts: 3
        window: 60s
    env_file:
      - .env.kumaslash
    depends_on:
      - postgres
  postgres:
    image: postgres:15-alpine
    volumes:
      - kumaslash-db:/var/lib/postgresql/data
    env_file:
      - .env.kumaslash
  # Optional
  adminer:
    image: adminer
    ports:
      - 8888:8080
    depends_on:
      - postgres
volumes:
  kumaslash-db:
```

Create an environment file, with a name you remember like `.env.kumaslash`, to store all the configuration for the bot.
The bot will not start if the environment variables is not configured correctly. If you don't want to create an
environment file you can declare them directly in the Docker Compose file. The once below is the most import ones:

```properties
# You can find the latest version over at https://github.com/users/ghostbear/packages/container/package/kumaslash
# You could also directly define this attribute in the Docker Compose file.
VERSION={LATEST_VERSION_HERE}

################
# Bot specific #
################

DISCORD_BOT_TOKEN={YOUR_TOKEN_HERE}
SPRING_R2DBC_URL=r2dbc:postgresql://postgres:5432/postgres
# SPRING_R2DBC_URL=r2dbc:postgresql://HOST:PORT/POSTGRES_DB
# SPRING_R2DBC_USERNAME=POSTGRES_USER
# SPRING_R2DBC_PASSWORD=POSTGRES_PASSWORD


################
#  PostgreSQL  #
################

# If you edit any of the PostgreSQL environment variables make
# sure the update the Spring R2DBC environment variables.
POSTGRES_PASSWORD=postgres
# POSTGRES_USER=postgres
# POSTGRES_DB=POSTGRES_USER
```

After creating both the Docker Compose file and the environment variables file, you can start up the services with
Docker Compose.

```shell
docker compose --project-name kumaslash --file /path/to/docker-compose.kumaslash.yml --env-file /path/to/.env.kumaslash up
```

</details>

## Develop

### Technology

- Java 20
- Gradle
- Spring Boot
    - Web Flux
    - R2DBC
    - GraphQL
- Discord4J
- PostgreSQL
- Flyway

### Setup

KumaSlash uses Spring Boot DevTools to avoid including the Discord bot token in the repository during development.
Instead, the token is placed in the `.spring-boot-devtools.properties` file, which is located in the `$HOME` directory.

**~/.spring-boot-devtools.properties**

```
discord.bot.token=your_token_here
```

### Running

Before running the application, you need to have Docker running in the background. Because KumaSlash
uses [Docker Compose Support](https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/#features.docker-compose)
from Spring Boot, which automatically starts up a Postgres server on application start.

KumaSlash is developed with IntelliJ IDEA and there is an included custom run configuration, to quickly start the
Discord Bot in development mode.
This configuration can be selected from the configuration picker next to the run button.

Alternatively, you can also use Gradle to achieve the same effect. To do this, run the following command:

```shell
./gradlew bootRun --args='--spring.profiles.active=dev'
```

This will activate the development profile for Spring Boot, which will turn on debug level logging and Spring Boot
DevTools.
