# Wildguide-spring

![App Version](https://img.shields.io/badge/dynamic/xml?url=https%3A%2F%2Fraw.githubusercontent.com%2FHenryDeLange%2FWildGuide-spring%2Fmain%2Fpom.xml&query=%2F*%5Blocal-name()%3D'project'%5D%2F*%5Blocal-name()%3D'version'%5D&label=version)
![GitHub License](https://img.shields.io/github/license/HenryDeLange/WildGuide-spring)

This repository is for the WildGuide backend (Spring, Java). Also see the related [WildGuide frontend](https://github.com/HenryDeLange/WildGuide-react).

[🦉 Live API](https://api.wildguide.mywild.co.za/swagger-ui/index.html)

## Development

![Top Language](https://img.shields.io/github/languages/top/HenryDeLange/WildGuide-spring)
![Maven Build](https://img.shields.io/github/actions/workflow/status/HenryDeLange/WildGuide-spring/spring-source-build.yml?label=maven%20build)
![Docker Deploy](https://img.shields.io/github/actions/workflow/status/HenryDeLange/WildGuide-spring/spring-docker-image-build.yml?label=docker%20image%20deploy)

### Setup

#### Environment Variables

See the [.env.local](./.env.local) file containing development values.

#### Properties

See the [application.yml](./src/main/resources/application.yml) file. (Properties can be overwritten using environment variables.)

### Build

This project written in `Java 23` using `Spring Boot`, and `Maven` to build.

`mvn clean verify -P prod`

## Production

### Setup

#### Docker Container
Create a Docker container using the `mywild/wildguide:latest` image.

#### Environment Variables

Configure the environment variables, declared in the [.env.production](./.env.production) file, for the container.

##### Encryption Keys

Use `openssl` to create the necessary keys:

```sh
mkdir ./keys
openssl genrsa -out ./keys/private_key.pem 2048
openssl rsa -in ./keys/private_key.pem -outform PEM -pubout -out ./keys/public_key.pem
openssl pkcs8 -topk8 -inform PEM -in ./keys/private_key.pem -outform PEM -nocrypt -out ./keys/private_key_pkcs8.pem
```

(In Windows you can use WSL to execute the commands.)

#### Volumes
Attach volumes for the `/app/data`, `/app/backups` and `/app/logs` folders.

### Deploy

Manually run the [Build and Publish Spring Docker Image](https://github.com/HenryDeLange/WildGuide-spring/actions/workflows/spring-docker-image-build.yml) GitHub Action in order to build and publish the new (_latest_) Docker [image](https://hub.docker.com/repository/docker/mywild/wildguide).

Switch the container over to the new image. _(The MyWild production environment on Azure will automatically switch over to the latest image.)_
