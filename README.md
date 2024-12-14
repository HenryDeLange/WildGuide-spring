# TODO:
- Can the Maven build generate a swagger yml file to be committed?

# WildEvents-spring
![App Version](https://img.shields.io/badge/dynamic/xml?url=https%3A%2F%2Fraw.githubusercontent.com%2FHenryDeLange%2FWildGuide-spring%2Fmain%2Fpom.xml&query=%2F*%5Blocal-name()%3D'project'%5D%2F*%5Blocal-name()%3D'version'%5D&label=version)
![GitHub License](https://img.shields.io/github/license/HenryDeLange/WildGuide-spring)

The Spring (Java) backend for WildGuide.

## Development

### Build
![Top Language](https://img.shields.io/github/languages/top/HenryDeLange/WildGuide-spring)
![Maven Build](https://img.shields.io/github/actions/workflow/status/HenryDeLange/WildGuide-spring/spring-source-build.yml?label=maven%20build)
![Docker Deploy](https://img.shields.io/github/actions/workflow/status/HenryDeLange/WildGuide-spring/spring-docker-build.yml?label=docker%20deploy)

This project written in `Java 23` using `Spring Boot`, and `Maven` to build.

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
#### SECURITY ####
JWT_AUDIENCE=development
# Set to the frontend's URL
CORS=*
# Use value from private_key_pkcs8.pem (only the base64 key, not the header/footer)
PRIVATE_KEY=...
# Use value from public_key.pem (only the base64 key, not the header/footer)
PUBLIC_KEY=...
```

In development you can also add `server.error.include-stacktrace=always` to the `.env` file to help troubleshoot exceptions.

#### Properties
See the [application.yml](./src/main/resources/application.yml) file.

These properties can be overwritten using environment variables.
