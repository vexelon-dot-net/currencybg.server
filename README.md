Currency BG Server
============================

[![CircleCI](https://dl.circleci.com/status-badge/img/gh/vexelon-dot-net/currencybg.server/tree/feature%2Fgcp-migration.svg?style=svg)](https://dl.circleci.com/status-badge/redirect/gh/vexelon-dot-net/currencybg.server/tree/feature%2Fgcp-migration)

Currency BG HTTP API Server

Read the [HTTP API documentation](docs/API.md) for details.

# Requirements

* Java `17`
* Google Cloud Firestore

# Development

To build the project run:

	./gradlew build

To update dependencies run:

    ./gradlew refreshVersions

# Configuration & Deployment

One needs to go to the Cloud Firestore and add all the supported sources manually. Copy the Firebase `adminsdk.json`
file
to `resources/`.

To configure the server use the environment vars:

    CBG_HOST=<listen address> // default ::1
    CBG_PORT=<listen port> // default 8080
    CBG_CFG_PATH=<directory path> // path to where server configurations will be saved

To access the API open:

    http://localhost:8080/api

# License

[GNU AGPL](LICENSE) 
