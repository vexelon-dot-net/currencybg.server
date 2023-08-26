Currency BG Server
============================

[![CircleCI](https://circleci.com/gh/vexelon-dot-net/currencybg.server/tree/master.svg?style=svg&circle-token=dbb483218ea63d7fa3551c6cc3c3b3fd95f99e1e)](https://circleci.com/gh/vexelon-dot-net/currencybg.server/tree/master)

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
