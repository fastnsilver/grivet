#!/usr/bin/env bash

set -e

if [ $# -ne 2 ]; then
    echo "Usage: ./show-log.sh standalone|pipeline {docker_image}"
    exit 1
fi

suffix=$1
docker_image=$2

export DOCKER_IP="host.docker.internal"

# Change directories
cd docker

# Display status of cluster
docker compose -f docker-compose.yml -f docker-compose-"$suffix.yml" logs $docker_image
