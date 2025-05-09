# krisefikser-backend

## Overview
This is the backend repository for the application Krisefikser. It is written in Java
Spring Boot using JDBC, and it is designed to be run in a Docker container. The application is intended 
to provide a robust backend solution for the Krisefikser project. The repository includes all necessary
files to build and run the backend of the application, including Docker configuration files.
## Table of Contents
- [Overview](#overview)
- [Installation](#installation)
- [Requirements](#requirements)
- [How to run](#how-to-run)
- [Configuration](#configuration)

## Installation

```bash
git clone https://github.com/idatt2106-2025-07/krisefikser-backend.git
```

## Prerequisites
- Docker desktop installed and running
- Env file with necessary environment variables (see .env.example)

## How to run
NB! first run might take some time (3-5 minutes)
Ensure that Docker desktop is running
```
cd krisefikser-backend
docker-compose up --build
```

## Configuration
To run the project an environment file is needed. This file should be named `.env` 
and should be placed in the root directory of the project. An example of the file can be found in `.env.example`.
To get access to the environment file please contact the team behind the project.