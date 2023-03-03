Currency BG Server
============================

[![CircleCI](https://circleci.com/gh/vexelon-dot-net/currencybg.server/tree/master.svg?style=svg&circle-token=dbb483218ea63d7fa3551c6cc3c3b3fd95f99e1e)](https://circleci.com/gh/vexelon-dot-net/currencybg.server/tree/master)

Currency BG HTTP API Server

Read the [HTTP API documentation](docs/API.md) for details.

# Requirements

* JDK `17`
* MySQL `5.5+` or MariaDB `10.0`

# Development

To build the project run:

	./gradlew build

To update dependencies run:

    ./gradlew refreshVersions

# Deployment

Create a MySQL database and use the DDL in `schemas` to create all required tables.

In order to deploy a local test version the following Java properties need to be setup, i.e.,

    CBG_CFG_PATH=<directory path> // path to where server configurations will be saved
    DB_HOST=<mysql hostname>
    DB_PORT=<mysql port>
    DB_NAME=<mysql database>
    DB_USERNAME=<mysql user>
    DB_PASSWORD=<mysql password>

To access the API open:

    http://localhost:8080/api

# License

[GNU AGPL](LICENSE) 
