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

echo "Adding files to local test folders"
az storage container create --name 'cvptestcontainer' --connection-string 'DefaultEndpointsProtocol=http;AccountName=devstoreaccount1;AccountKey=Eby8vdM02xNOcqFlqUwJPLlmEtlCDXJ1OUzFT50uSRZ6IFsuFq2UVErCz4I6tq/K1SZFPTOtr/KBHBeksoGMGw==;BlobEndpoint=http://127.0.0.1:10000/devstoreaccount1;'



az storage blob upload -f README.md -c cvptestcontainer -n audiostream999996/AB-0144-GF0YM700_2020-10-20-11.30.00.150-UTC_0.mp4  --connection-string "DefaultEndpointsProtocol=http;AccountName=devstoreaccount1;AccountKey=Eby8vdM02xNOcqFlqUwJPLlmEtlCDXJ1OUzFT50uSRZ6IFsuFq2UVErCz4I6tq/K1SZFPTOtr/KBHBeksoGMGw==;BlobEndpoint=http://127.0.0.1:10000/devstoreaccount1"
az storage blob upload -f README.md -c cvptestcontainer -n audiostream999996/00_2020-10-20-09.05.50.150-UTC_0.mp4 --connection-string "DefaultEndpointsProtocol=http;AccountName=devstoreaccount1;AccountKey=Eby8vdM02xNOcqFlqUwJPLlmEtlCDXJ1OUzFT50uSRZ6IFsuFq2UVErCz4I6tq/K1SZFPTOtr/KBHBeksoGMGw==;BlobEndpoint=http://127.0.0.1:10000/devstoreaccount1"
az storage blob upload -f README.md -c cvptestcontainer -n audiostream999996/0144-GF0YM622_2020-10-20-10.00.30.150-UTC_0.mp4 --connection-string "DefaultEndpointsProtocol=http;AccountName=devstoreaccount1;AccountKey=Eby8vdM02xNOcqFlqUwJPLlmEtlCDXJ1OUzFT50uSRZ6IFsuFq2UVErCz4I6tq/K1SZFPTOtr/KBHBeksoGMGw==;BlobEndpoint=http://127.0.0.1:10000/devstoreaccount1"
az storage blob upload -f README.md -c cvptestcontainer -n audiostream999996/CV0144-HL0NP630_2020-10-20-13.10.20.150-UTC_0.mp4 --connection-string "DefaultEndpointsProtocol=http;AccountName=devstoreaccount1;AccountKey=Eby8vdM02xNOcqFlqUwJPLlmEtlCDXJ1OUzFT50uSRZ6IFsuFq2UVErCz4I6tq/K1SZFPTOtr/KBHBeksoGMGw==;BlobEndpoint=http://127.0.0.1:10000/devstoreaccount1"


az storage blob upload -f README.md -c cvptestcontainer -n audiostream999996/0144-GF0YM622_2020-10-20-10.00.30.150-UTC_0.mp4 --connection-string "DefaultEndpointsProtocol=http;AccountName=devstoreaccount1;AccountKey=Eby8vdM02xNOcqFlqUwJPLlmEtlCDXJ1OUzFT50uSRZ6IFsuFq2UVErCz4I6tq/K1SZFPTOtr/KBHBeksoGMGw==;BlobEndpoint=http://127.0.0.1:10000/devstoreaccount1"


az storage blob upload -f README.md -c cvptestcontainer -n audiostream999997/FM-0123-BV20D01_2020-11-04-14.56.32.819-UTC_0.mp4 --connection-string "DefaultEndpointsProtocol=http;AccountName=devstoreaccount1;AccountKey=Eby8vdM02xNOcqFlqUwJPLlmEtlCDXJ1OUzFT50uSRZ6IFsuFq2UVErCz4I6tq/K1SZFPTOtr/KBHBeksoGMGw==;BlobEndpoint=http://127.0.0.1:10000/devstoreaccount1"
az storage blob upload -f README.md -c cvptestcontainer -n audiostream999997/FM-0123-BV20D01_2020-11-04-14.56.32.819-UTC_1.mp4 --connection-string "DefaultEndpointsProtocol=http;AccountName=devstoreaccount1;AccountKey=Eby8vdM02xNOcqFlqUwJPLlmEtlCDXJ1OUzFT50uSRZ6IFsuFq2UVErCz4I6tq/K1SZFPTOtr/KBHBeksoGMGw==;BlobEndpoint=http://127.0.0.1:10000/devstoreaccount1"
az storage blob upload -f README.md -c cvptestcontainer -n audiostream999997/FM-0123-BV20D01_2020-11-04-14.56.32.819-UTC_2.mp4 --connection-string "DefaultEndpointsProtocol=http;AccountName=devstoreaccount1;AccountKey=Eby8vdM02xNOcqFlqUwJPLlmEtlCDXJ1OUzFT50uSRZ6IFsuFq2UVErCz4I6tq/K1SZFPTOtr/KBHBeksoGMGw==;BlobEndpoint=http://127.0.0.1:10000/devstoreaccount1"
az storage blob upload -f README.md -c cvptestcontainer -n audiostream999997/FM-0123-BV20D01_2020-11-04-14.56.32.819-UTC_3.mp4 --connection-string "DefaultEndpointsProtocol=http;AccountName=devstoreaccount1;AccountKey=Eby8vdM02xNOcqFlqUwJPLlmEtlCDXJ1OUzFT50uSRZ6IFsuFq2UVErCz4I6tq/K1SZFPTOtr/KBHBeksoGMGw==;BlobEndpoint=http://127.0.0.1:10000/devstoreaccount1"
az storage blob upload -f README.md -c cvptestcontainer -n audiostream999997/FM-0123-BV20D01_2020-11-04-14.56.32.819-UTC_4.mp4 --connection-string "DefaultEndpointsProtocol=http;AccountName=devstoreaccount1;AccountKey=Eby8vdM02xNOcqFlqUwJPLlmEtlCDXJ1OUzFT50uSRZ6IFsuFq2UVErCz4I6tq/K1SZFPTOtr/KBHBeksoGMGw==;BlobEndpoint=http://127.0.0.1:10000/devstoreaccount1"

az storage blob upload -f README.md -c cvptestcontainer -n audiostream999998/FM-0456-CD30D01_2020-11-05-15.36.42.619-UTC_0.mp4 --connection-string "DefaultEndpointsProtocol=http;AccountName=devstoreaccount1;AccountKey=Eby8vdM02xNOcqFlqUwJPLlmEtlCDXJ1OUzFT50uSRZ6IFsuFq2UVErCz4I6tq/K1SZFPTOtr/KBHBeksoGMGw==;BlobEndpoint=http://127.0.0.1:10000/devstoreaccount1"
az storage blob upload -f README.md -c cvptestcontainer -n audiostream999998/FM-0456-CD30D01_2020-11-05-15.36.42.619-UTC_1.mp4 --connection-string "DefaultEndpointsProtocol=http;AccountName=devstoreaccount1;AccountKey=Eby8vdM02xNOcqFlqUwJPLlmEtlCDXJ1OUzFT50uSRZ6IFsuFq2UVErCz4I6tq/K1SZFPTOtr/KBHBeksoGMGw==;BlobEndpoint=http://127.0.0.1:10000/devstoreaccount1"
az storage blob upload -f README.md -c cvptestcontainer -n audiostream999998/FM-0456-CD30D01_2020-11-05-15.36.42.619-UTC_2.mp4 --connection-string "DefaultEndpointsProtocol=http;AccountName=devstoreaccount1;AccountKey=Eby8vdM02xNOcqFlqUwJPLlmEtlCDXJ1OUzFT50uSRZ6IFsuFq2UVErCz4I6tq/K1SZFPTOtr/KBHBeksoGMGw==;BlobEndpoint=http://127.0.0.1:10000/devstoreaccount1"

az storage blob upload -f README.md -c cvptestcontainer -n audiostream999999/FM-0789-EF31D01_2020-11-06-16.26.12.419-UTC_0.mp4 --connection-string "DefaultEndpointsProtocol=http;AccountName=devstoreaccount1;AccountKey=Eby8vdM02xNOcqFlqUwJPLlmEtlCDXJ1OUzFT50uSRZ6IFsuFq2UVErCz4I6tq/K1SZFPTOtr/KBHBeksoGMGw==;BlobEndpoint=http://127.0.0.1:10000/devstoreaccount1"

az storage blob upload -f README.md -c cvptestcontainer -n audiostream115/FM-0111-testfile200M_2020-01-01-11.11.11.123-UTC_0.mp4 --connection-string "DefaultEndpointsProtocol=http;AccountName=devstoreaccount1;AccountKey=Eby8vdM02xNOcqFlqUwJPLlmEtlCDXJ1OUzFT50uSRZ6IFsuFq2UVErCz4I6tq/K1SZFPTOtr/KBHBeksoGMGw==;BlobEndpoint=http://127.0.0.1:10000/devstoreaccount1"



docker-compose ${COMPOSE_FILE} up -d clamav

echo "LOCAL ENVIRONMENT SUCCESSFULLY STARTED"

docker-compose ${COMPOSE_FILE} logs -f
