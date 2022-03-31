# Oengus developer environment

This instruction provides steps on how to set up a development environment to work on Oengus backend.
See [DOCKER.md](./DOCKER.md) for Docker instructions.

## Prerequisites

Install packages which are needed for running Oengus. JDK 16 or later is required. Just install the latest JDK available
in your distribution. For example, for Java 17 on a Debian-based distro:

```shell
$ sudo apt install openjdk-17-jdk postgresql
```

For other ways to install JDK and Postgres, see their respective websites:

- JDK, e.g. [Oracle JDK](https://www.java.com/en/download/) or [OpenJDK](http://openjdk.java.net/).
- https://www.postgresql.org/download/

## Source code

```shell
$ git clone git@github.com:esamarathon/oengusio.git
$ cd oengusio
```

## Postgres

If you haven't used Postgres before, set up a password for the connection to the database:

```shell
$ sudo su - postgres
postgres@hostname:~$ psql
postgres=# ALTER USER postgres PASSWORD '<your password here>';
```

Create database `oengus`:

```shell
$ sudo su - postgres
postgres@hostname:~$ createdb oengus
```

## Discord integration

Discord integration is needed to allow you to login into your Oengus dev environment of with your real Discord account.

Follow [these instructions][Discord instructions] to create a Discord client ID and client secret values. Name your
application something like `<your name>-oengus-dev`.

## IntelliJ IDEA run config

The easiest way for developers to run Oengus is from an [IntelliJ IDEA run configuration][IntelliJ run configs].

1. Open the project in IntelliJ IDEA by selecting Oengus's file `build.gradle` in the "Open..." dialog.
2. Open the "Run/Debug Configurations" dialog.
3. Make a copy of the config `OengusApplicationTemplate` for yourself by clicking the "Copy Configuration" button above
   the list.
4. Rename the new config.
5. Edit the environment variables to insert your own values for:

   ```
   DISCORD_CLIENT_ID
   DISCORD_CLIENT_SECRET
   DB_PASSWORD
   ```
6. Run the configuration that you've created. The backend of Oengus should now be running in your IDE!

[Discord instructions]: <https://github.com/SinisterRectus/Discordia/wiki/Setting-up-a-Discord-application>
[IntelliJ run configs]: <https://www.jetbrains.com/help/idea/run-debug-configuration.html>
