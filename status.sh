#!/usr/bin/env bash

set -e

if [ $# -ne 1 ]; then
    echo "Usage: ./status.sh standalone|pipeline"
    exit 1
fi

suffix=$1

export DOCKER_IP="host.docker.internal"

# Change directories
cd docker

# Display status of cluster
docker compose -f docker-compose.yml -f docker-compose-"$suffix.yml" ps -a
