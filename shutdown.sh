#!/usr/bin/env bash

set -e

if [ $# -ne 1 ]; then
    echo "Usage: ./shutdown.sh standalone|pipeline"
    exit 1
fi

suffix=$1

# Change directories
cd docker

# Export the active docker machine IP
export DOCKER_IP=$(docker-machine ip $(docker-machine active))

# docker-machine doesn't exist in Linux, assign default ip if it's not set
DOCKER_IP=${DOCKER_IP:-0.0.0.0}

# Remove existing containers
docker-compose -f docker-compose.yml -f docker-compose-$suffix.yml down
