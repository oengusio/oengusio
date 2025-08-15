# Oengus  [![translateBadge]][translateUrl]

Oengus is a web application that helps manage speedrun marathons

https://oengus.io

Note that the front-end lives on this repository [https://github.com/oengusio/oengus-webapp](https://github.com/oengusio/oengus-webapp).

## How to run on your computer

_**Disclaimer**: self-hosted instances are not allowed to use the Oengus branding._

### Requirements

- Java JDK 17
- PostgreSQL 10+

### Instructions

#### Setup

A docker image is hosted on docker hub https://hub.docker.com/repository/docker/oengusio/backend

On PostgreSQL, create a database named `oengus`

Fill the following fields and add them to your environment variables, this can be done locally with your favourite IDE but we strongly recommend Intellij IDEA. For Discord/Twitch, only one of them is required so you can login. We recommend using [Discord](https://github.com/SinisterRectus/Discordia/wiki/Setting-up-a-Discord-application) since it's the easiest to setup.

The redirect urls for this are:
- Login: \[base_url]/login/discord
- Sync: \[base_url]/user/settings/sync/discord

Alternatively you can just use username/password login

```
AMQP_URI=amqp://localhost/;
AMQP_USERNAME=guest;
AMQP_PASSWORD= guest;
JWT_SECRET=<random string>;
PAYPAL_CLIENT_ID=; //Unrequired if you don't work on donations
PAYPAL_CLIENT_SECRET=; //Unrequired if you don't work on donations
DISCORD_CLIENT_ID=;
DISCORD_CLIENT_SECRET=;
DISCORD_BOT_TOKEN=;
TWITCH_CLIENT_ID=;
TWITCH_CLIENT_SECRET=;
DB_URL=jdbc:postgresql://localhost:5432/oengus;
DB_USERNAME=;
DB_PASSWORD=;
BASE_URL=http://localhost:4200;
OAUTH_ORIGINS=http://localhost:4200,https://oengus.io;
SENTRY_DSN=;
SENTRY_TRACES_SAMPLE_RATE=1.0;
SENTRY_ENVIRONMENT=local;
```

#### Dependencies

```shell script
./gradlew dependencies
```

#### Run

##### Docker (recommended for production)

Docker setup instructions are in [docs/DOCKER.md](./docs/DOCKER.md).

1. Copy `docker-compose.yml` to `docker-compose.override.yml`
2. Fill in the environment variables in `docker-compose.override.yml`
3. Run `docker-compose up --build`

##### IDE

Start `OengusApplication.java` with your favourite IDE. On first startup the database will be initialized automatically.
See [docs/DEVELOPER.md](./docs/DEVELOPER.md) for detailed instructions on how to set up a development environment.

## Support

Please join the official [Discord server](https://discord.gg/ZZFS8YT) for questions and support

## Contributing
Pull requests are welcome. For major changes, please open an issue first to discuss what you would like to change.

Please make sure to update tests as appropriate (if tests exist).

### Translating
This project uses weblate for its translations, you can contribute by visiting https://hosted.weblate.org/projects/oengusio/exports

## License
[GNU AGPL v3.0](https://choosealicense.com/licenses/agpl-3.0/)


[translateBadge]: https://hosted.weblate.org/widgets/oengusio/-/exports/svg-badge.svg
[translateUrl]: https://hosted.weblate.org/engage/oengusio/
