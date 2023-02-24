# simple-expense-tracker

## Overall application architecture

The **_simple-expense-tracker_** application consists of the three main modules, represented by directories of the same
name in the root directory of the project:

- **app-models**  - describes the data models used in communication between client and server.
- **app-client** - a simple client-side SPA (Single Page Application) using React.js and Typescript.
- **app-server** - server-side application providing authentication, REST-API and data-persistence capabilities.

A more detailed description of each of the modules can be found in the directory corresponding to the module.

## Technologies required for the build and development

- Java 17
- Node.js 18.14.2
  > **_NOTE:_** Node.js of the required version is specified in the gradle.properties file and does not require manually
  installation - during the project build through gradle, all the necessary dependencies (including Node.js itself) will
  be pulled automatically. For the convenience of local client-side development, it is recommended to install Node.js of
  the required version yourself. It is also recommended to use NVM, which allows you to have many versions of Node.js on
  one machine and easily switch between them at any time.
- Postgres 12
  > **_NOTE:_** The required version of Postgres can be easily installed as a Docker container - just run
  docker-compose.dev.yml in the project's root directory.

## Useful Gradle tasks with definitions

- `./gradlew :app-models:build` - generates application model - Java for the server and Typescript for the client
- `./gradlew :app-client:build` - builds client-side application
- `./gradlew :app-server:build` - builds server-side application
- `./gradlew :stage`            - assembles an executable JAR with client/server parts, includes the above tasks
