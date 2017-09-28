Currency BG Server
============================

[![CircleCI](https://circleci.com/gh/vexelon-dot-net/currencybg.server/tree/master.svg?style=svg&circle-token=dbb483218ea63d7fa3551c6cc3c3b3fd95f99e1e)](https://circleci.com/gh/vexelon-dot-net/currencybg.server/tree/master)

Currency BG RESTful web service

HTTP [API documentation](docs/API.md)

# Requirements

  * JDK `1.8`
  * MySQL `5.5`
  * Tomcat `8` or Jetty `9`

# Development

Install [Gradle](https://gradle.org/gradle-download/) or use the `./gradlew` script.

If you use Eclipse, you may generate the Eclipse project files by running:

	./gradlew eclipse

To build the project run:

	./gradlew build

# Deployment

Create a MySQL database and use the DDL in `schemas` to create all required tables.

In order to deploy a local test version the following Java properties need to be setup, i.e.,

    CBG_CFG_PATH=<directory path> // path to where server configurations will be saved
    DB_HOST=<mysql hostname>
    DB_PORT=<mysql port>
    DB_NAME=<mysql database>
    DB_USERNAME=<mysql user>
    DB_PASSWORD=<mysql password>

To run locally:

	./gradlew tomcatRun \
      -DCBG_CFG_PATH=<directory path> \
      -DDB_HOST=<host> -DDB_PORT=<port> -DDB_NAME=<database> \
      -DDB_USERNAME=<username> -DDB_PASSWORD=<password> 

To access the API open:

    http://localhost:8090/api

# License

[GNU AGPL](LICENSE) 
