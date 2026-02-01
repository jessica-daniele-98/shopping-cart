#!/bin/bash
set -e

echo "Building..."
docker-compose -f docker-compose.yaml build

echo "Start docker compose..."
docker-compose -f docker-compose.yaml up -d --no-build

echo "Running tests..."
cd .. && mvn -f docker-compose.yaml clean install

echo "Stopping container..."
docker-compose -f docker-compose.yaml down

echo "Finish script"