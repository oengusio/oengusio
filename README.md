# Oengus  [![translateBadge]][translateUrl]

Oengus is a web application that helps manage speedrun marathons

https://oengus.io

Note that the front-end lives on this repository [https://github.com/esamarathon/oengus-webapp](https://github.com/esamarathon/oengus-webapp).

## How to run on your computer

### Requirements

- Java JDK 16
- PostgreSQL 10+

### Instructions

#### Setup

On PostgreSQL, create a database named `oengus`

Fill the following fields and add them to your environment variables (can be done locally with your favourite IDE). For Discord/Twitch/Twitter, only one of them is required so you can login. We recommend using [Discord](https://github.com/SinisterRectus/Discordia/wiki/Setting-up-a-Discord-application) since it's the easiest to setup 

```
JWT_SECRET=<random string>;
PAYPAL_CLIENT_ID=; //Unrequired if you don't work on donations
PAYPAL_CLIENT_SECRET=; //Unrequired if you don't work on donations
DISCORD_CLIENT_ID=;
DISCORD_CLIENT_SECRET=;
DISCORD_BOT_TOKEN=;
TWITCH_CLIENT_ID=;
TWITCH_CLIENT_SECRET=;
TWITTER_CONSUMER_KEY=;
TWITTER_CONSUMER_SECRET=;
TWITTER_ACCESS_TOKEN=;
TWITTER_ACCESS_TOKEN_SECRET=;
DB_URL=jdbc:postgresql://localhost:5432/oengus;
DB_USERNAME=;
DB_PASSWORD=;
BASE_URL=http://localhost:4200;
```

#### Dependencies

```shell script
./gradlew dependencies
```

#### Run

##### Docker (recommended)
Copy `docker-compose.yml` to `docker-compose.override.yml` and ill in the environment variables, then run `docker-compose up --build`

##### IDE
Start Application.java with your favourite IDE. On first startup the database will be initialized automatically.

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
