#!/usr/bin/env bash

set -e

if [ $# -ne 2 ]; then
    echo "Usage: ./show-log.sh standalone|pipeline {docker_image}"
    exit 1
fi

suffix=$1
docker_image=$2

# Change directories
cd docker

# Export the active docker machine IP
export DOCKER_IP=$(docker-machine ip $(docker-machine active))

# docker-machine doesn't exist in Linux, assign default ip if it's not set
DOCKER_IP=${DOCKER_IP:-0.0.0.0}

# Display status of cluster
docker-compose -f docker-compose.yml -f docker-compose-$suffix.yml logs $docker_image
