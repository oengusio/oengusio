# Oengus

Oengus is a web application that helps managins speedrun marathons

https://oengus.io

## How to run on your computer

### Requirements

- Java JDK 12
- NodeJS 10+
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
TWITCH_LOGIN_CLIENT_ID=;
TWITCH_LOGIN_CLIENT_SECRET=;
TWITCH_SYNC_CLIENT_ID=;
TWITCH_SYNC_CLIENT_SECRET=;
TWITTER_CONSUMER_KEY=;
TWITTER_CONSUMER_SECRET=;
TWITTER_ACCESS_TOKEN=;
DB_URL=jdbc:postgresql://localhost:5432/oengus;
DB_USERNAME=;
DB_PASSWORD=;
BASE_URL=http://localhost:4200;
```

#### Dependencies

```shell script
./mvnw clean install
cd src/webapp
npm install
```

#### Run

Back : Start Application.java with your favourite IDE. On first startup the database will be initialized automatically.

Front : 
```shell script
cd src/webapp
npm start
```

## Support

Please join the official [Discord server](https://discord.gg/ZZFS8YT) for questions and support

## Contributing
Pull requests are welcome. For major changes, please open an issue first to discuss what you would like to change.

Please make sure to update tests as appropriate (if tests exist).

## Project status

After working for almost a full year on this project, developments start to slow down. We're actively looking for maintainers to keep adding new features and fix bugs. 

## License
[GNU GPL v3.0](https://choosealicense.com/licenses/gpl-3.0/)
