Oengus Docker instructions
==========================

To run Oengus backend for development purposes, you don't need to use Docker. Using IntelliJ IDEA for development is
both easier and more convenient: [IntelliJ IDEA instructions](./DEVELOPER.md). However, if you do need to use the Docker
image, follow the instruction below on how to use Oengus in Docker in Ubuntu or other Debian-based GNU/Linux
distributions.

Installation
------------

Install packages listed in "Prerequisites" section of [DEVELOPER.md](./DEVELOPER.md). As of March 2022, Ubuntu's
docker-compose is way outdated. Because of that, you'll have to install Docker from Docker's packages:
https://docs.docker.com/engine/install/ubuntu/

Docker setup
------------

If you haven't used Docker before, you need to do the following:

1. Add yourself to the `docker` group to have access to the daemon:

   ```shell
   $ sudo usermod -a -G docker $USER
   ```
2. To get the updated groups in text shells you can just re-log in, but in GUI shells, you'll have to re-log in via
   their GUI.

   ```shell
   $ sudo su - $USER
   ```
3. Start up the docker daemon (assuming systemd-based distro):

   ```shell
   $ sudo systemctl start docker
   ```

## Get Oengus Docker image

Get the Docker image of Oengus from [Docker Hub][Docker image]:

```shell
$ docker pull oengusio/backend
```

[Docker image]: <https://hub.docker.com/repository/docker/oengusio/backend>
