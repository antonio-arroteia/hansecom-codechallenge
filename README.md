## Table of Contents

- [Introduction](#introduction)
- [Pre Requisites](#prerequisites)
- [Getting Started](#getting-started)
- [API Documentation](#documentation)
- [Usage](#usage)
- [Future Implementations](#future)
- [Blockers](#blockers)
- [Personal Considerations](#considerations)


## Introduction

This Monitoring Job API lets you create from 1 to 5 Monitoring Jobs that will run periodically based on an interval of the user's choosing.
This monitoring job will ping the specified url and store the result from it.


## Pre Requisites

- Java 21
- Postgres 14
- Docker 20.10.14 (Optional)


## Getting Started
Run a Postgres server locally on port 5432, and create the db "codechallenge_db" with the cli command 'createdb codechallenge_db'. 
This is because the integration tests run on build time and for that you need a running db instance.
Clone the project to your local environment and build it using gradle command at the root project level "./gradlew clean build".
After, you can start the project on a docker container:
- At root level, run in sequence the commands "docker-compose build" to build the image with the latest build and "docker-compose up" to run the image
Either way the application should start right away! 

## API Documentation

With the service running, access the Swagger UI at http://localhost:8080/hansecom-codechallenge/swagger-ui/index.html with a ready to use client interface for calling the APIs endpoints


## Testing
You can easily test the running API using the Swagger UI as specified above.
You can also use an API Manager like Postman, following the swagger documentation.

The project's unit and integration tests run during the application build, but you can run it at any time by using the command "./gradlew test" from the root.

## Blockers


## Future Implementations

This project can be considered a proper POC, but in order for it to be used in a real production scenario it would benefit a lot from achieving these future goals: 
- Profiling (separate running context based on profile (dev, prod, test, etc)
- Security
- Caching
- Cloud Infrastructure (like exemplified in 'infrastructure/cf-template.yaml')
- CORS Restrictions
- Pipeline triggering (like exemplified in 'cicd/pipeline.yaml')


## Personal Considerations

It has been 2 years since developing with Java. For the last 2 years I've been developing entirely with Kotlin so was fun to feel the contrast between these 2 
JVM languages. I had almost forgotten what is like to have NPEs during development, Kotlin has spoiled me!
