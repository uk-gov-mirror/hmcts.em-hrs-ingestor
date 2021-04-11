#!/bin/bash

## Usage: ./docker/dependencies/start-local-environment

echo "Please ensure you have the relevant branch of hrs-api, started the local environment dependencies - and then run ./gradlew bootRun"
echo "https://github.com/hmcts/em-hrs-api/blob/master/docker/dependencies/start-local-environment.sh"


# Set variables
COMPOSE_FILE="-f docker-compose-dependencies.yml"

# Start all other images
echo "Starting dependencies..."
docker-compose ${COMPOSE_FILE} build

docker-compose ${COMPOSE_FILE} up -d azure-storage-emulator-azurite-cvp

az storage container create --name 'emhrstestcvpcontainer' --connection-string 'DefaultEndpointsProtocol=http;AccountName=devstoreaccount1;AccountKey=Eby8vdM02xNOcqFlqUwJPLlmEtlCDXJ1OUzFT50uSRZ6IFsuFq2UVErCz4I6tq/K1SZFPTOtr/KBHBeksoGMGw==;BlobEndpoint=http://127.0.0.1:20000/devstoreaccount1;'
az storage blob upload -f README.md -c emhrstestcvpcontainer -n audiostream999999/FM-0115-BV20D01_2020-11-04-14.56.32.819-UTC_0.mp4 --connection-string "DefaultEndpointsProtocol=http;AccountName=devstoreaccount1;AccountKey=Eby8vdM02xNOcqFlqUwJPLlmEtlCDXJ1OUzFT50uSRZ6IFsuFq2UVErCz4I6tq/K1SZFPTOtr/KBHBeksoGMGw==;BlobEndpoint=http://127.0.0.1:20000/devstoreaccount1"


docker-compose ${COMPOSE_FILE} up -d clamav

echo "LOCAL ENVIRONMENT SUCCESSFULLY STARTED"

docker-compose ${COMPOSE_FILE} logs -f
