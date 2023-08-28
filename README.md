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

	./gradlew clean assemble

To update dependencies run:

    ./gradlew refreshVersions

To deploy locally one must first configure one or more of the following env vars:

    CBG_CFG_PATH=<directory path> // path to where server configurations will be saved
    CBG_HOST=<listen address> // optional, default ::1
    CBG_PORT=<listen port> // optional, default 8080

# Deployment

Create a new Firebase project. Add a Firestore database and add all supported `sources` to manually.

Copy the Firebase authentication `adminsdk.json` file to `resources/`.

Create a new Google Cloud project and set its name to the `GCP_PROJECT_ID` property in `gradle.properties`.

Create a new configuration file at `etc-prod/cbg.properties`.

Finally, to deploy to GCP run:

    ./gradlew appengineDeploy

# License

[GNU AGPL](LICENSE) 
