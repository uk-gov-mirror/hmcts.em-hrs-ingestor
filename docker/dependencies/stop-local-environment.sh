#!/bin/bash

## Usage: ./docker/dependencies/stop-local-environment

docker-compose ${COMPOSE_FILE} down --remove-orphans
docker-compose ${COMPOSE_FILE} logs -f
