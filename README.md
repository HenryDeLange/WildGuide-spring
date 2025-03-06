# Wildguide-spring

![App Version](https://img.shields.io/badge/dynamic/xml?url=https%3A%2F%2Fraw.githubusercontent.com%2FHenryDeLange%2FWildGuide-spring%2Fmain%2Fpom.xml&query=%2F*%5Blocal-name()%3D'project'%5D%2F*%5Blocal-name()%3D'version'%5D&label=version)
![GitHub License](https://img.shields.io/github/license/HenryDeLange/WildGuide-spring)

This repository is for the WildGuide backend (Spring, Java). Also see the related [WildGuide frontend](https://github.com/HenryDeLange/WildGuide-react).

[Live API](https://api.wildguide.mywild.co.za/swagger-ui/index.html)

## Development

### API

See the [swagger.json](./swagger.json) file.

### Build

![Top Language](https://img.shields.io/github/languages/top/HenryDeLange/WildGuide-spring)
![Maven Build](https://img.shields.io/github/actions/workflow/status/HenryDeLange/WildGuide-spring/spring-source-build.yml?label=maven%20build)
![Docker Deploy](https://img.shields.io/github/actions/workflow/status/HenryDeLange/WildGuide-spring/spring-docker-image-build.yml?label=docker%20image%20deploy)

This project written in `Java 23` using `Spring Boot`, and `Maven` to build.

`mvn clean verify -P prod`

### Setup

#### Encryption Keys

Use `openssl` to create the necessary keys:

```sh
mkdir ./keys
openssl genrsa -out ./keys/private_key.pem 2048
openssl rsa -in ./keys/private_key.pem -outform PEM -pubout -out ./keys/public_key.pem
openssl pkcs8 -topk8 -inform PEM -in ./keys/private_key.pem -outform PEM -nocrypt -out ./keys/private_key_pkcs8.pem
```

(In Windows you can use WSL to execute the commands.)

#### Environment Variables

Create an `.env` file in the root folder containing the following information (use your own values):

```properties
##################
#### SECURITY ####

# Set to the environment identifier
JWT_AUDIENCE=development

# Set to the frontend's URL
CORS=*

# Use value from private_key_pkcs8.pem (only the base64 key, not the header/footer)
PRIVATE_KEY=...

# Use value from public_key.pem (only the base64 key, not the header/footer)
PUBLIC_KEY=...

##########################
#### DEVELOPMENT ONLY ####
mywild.app.devMode=true
mywild.wildguide.page-size=10
spring.h2.console.enabled=true
spring.h2.console.path=/h2-console
logging.level.console=TRACE
logging.level.mywild=TRACE
logging.level.org.springframework.security=DEBUG
logging.level.org.springframework.jdbc.core=DEBUG
logging.level.org.springframework.jdbc.datasource=DEBUG
```

#### Properties

See the [application.yml](./src/main/resources/application.yml) file.

These properties can be overwritten using environment variables.
