Currency BG Server
============================

[![CircleCI](https://circleci.com/gh/vexelon-dot-net/currencybg.server/tree/master.svg?style=svg&circle-token=dbb483218ea63d7fa3551c6cc3c3b3fd95f99e1e)](https://circleci.com/gh/vexelon-dot-net/currencybg.server/tree/master)

Currency BG RESTful web service

HTTP API [documentation](docs/API.md)

# Requirements

  * JDK `1.8`
  * MySQL `5.5`
  * Tomcat `7.+`

# Development

Install [Gradle](https://gradle.org/gradle-download/) `4.+` or use the `./gradlew` script.

Create a MySQL database called `currencybg` and run the DDL in `schemas` to create all required tables.

In order to deploy the local test version you need to first setup the following environment variables, i.e.,

    CBG_CFG_PATH=<directory path> // path to where server configurations will be saved
    OPENSHIFT_MYSQL_DB_HOST=<mysql hostname>
    OPENSHIFT_MYSQL_DB_PORT=<mysql port>
    OPENSHIFT_MYSQL_DB_USERNAME=<mysql user>
    OPENSHIFT_MYSQL_DB_PASSWORD=<mysql password>

To generate Eclipse project files run:

	./gradlew eclipse

To build the project run:

	./gradlew clean build

To start a local test version run:

	./gradlew tomcatRun

# License

[GNU AGPL](LICENSE) 
