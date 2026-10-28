# VCS

## Description

A version control system built from scratch in Java, with a command-line interface and a REST API.

## Features

- Initialize repositories (`init`)
- Stage changes (`add`)
- Remove tracked files (`rm`)
- View repository status (`status`)
- Create commits (`commit`)
- View commit history (`log`)
- Create and delete branches (`branch`)
- Checkout files and branches (`checkout`)
- Reset repository state (`reset`)
- Merge branches (`merge`)

## Usage

### REST API

Have a look at [Swagger UI]() for the available endpoints and their descriptions. You can also try out API requests 
directly from your browser.

### CLI

To view the available commands and their usage, run:

```bash
java -jar <cli-jar> help
```

#### Example

```bash
$ alias vcs='java -jar <cli-jar>'
$ vcs init
$ vcs add README.md
$ vcs commit 'Initial commit'
$ vcs log
commit 6472d230bfacf3f1e9599167a483c7d6fc651af3
Date: Wed Oct 28 06:55:31 IST 2026

    Initial commit
```

## Running Locally

### Prerequisites

- Java 25
- PostgreSQL 18

### REST API Server

1. Configure the required environment variables using [.env.example](.env.example) as a reference.
2. Start the server using:

    ```bash
    java -jar <api-jar>
    ```

### CLI

Run the CLI using:

```bash
java -jar <cli-jar>
```

## Building

### JAR Files

From the project root, run the following command to build the API and the CLI JARs:

```bash
./mvnw clean package
```

The generated JAR files will be available at:

- **API:** `vcs-api/target/vcs-api-<version>.jar`
- **CLI:** `vcs-cli/target/vcs-cli-<version>.jar`

### Docker Image

After building the JAR files, run the following command from the project root to build the API Docker image:

```bash
docker build -t vcs:<version> .
```
