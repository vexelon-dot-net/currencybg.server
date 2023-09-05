Currency BG Server
============================

[![CircleCI](https://dl.circleci.com/status-badge/img/gh/vexelon-dot-net/currencybg.server/tree/master.svg?style=svg)](https://dl.circleci.com/status-badge/redirect/gh/vexelon-dot-net/currencybg.server/tree/master)

Currency BG HTTP API Server

Read the [HTTP API documentation](docs/API.md) for details.

# Requirements

* Java `17`
* GCP project
* Firestore on Firebase

# Development

To build the project run:

	./gradlew clean assemble

To deploy locally one must first configure one or more of the following env vars:

    CBG_CFG_PATH=<directory path> // path to where server configurations will be saved
    CBG_HOST=<listen address> // optional, default ::1
    CBG_PORT=<listen port> // optional, default 8080

To run locally execute:

    ./gradlew run

# Deployment

Create a new Firebase project.

Set the Firebase project id to the `GCP_PROJECT_ID` property in `gradle.properties`.

Copy the Firebase service account json file to `resources/`.

Add all supported `sources` manually to the Firestore database.

Create a production configuration file at `src/main/resources/cbg.properties`. Set the `gcp.project.id`
and `gcp.firebase.url` configuration properties.

To deploy to GCP run:

    ./gradlew appengineDeploy

To monitor the app on AE run:

    gcloud app logs tail -s default

# License

[GNU AGPL](LICENSE) 
