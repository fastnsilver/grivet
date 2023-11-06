#!/usr/bin/env bash

set -e

if [ $# -ne 2 ]; then
    echo "Usage: ./show-log.sh standalone|pipeline {docker_image}"
    exit 1
fi

suffix=$1
docker_image=$2

# Export the active docker machine IP
if ! command -v docker-machine &> /dev/null
then
  export DOCKER_IP=$(docker-machine ip $(docker-machine active))
fi

if [ -z "$DOCKER_IP" ]; then
	SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
	if [ -f "$SCRIPT_DIR/.local" ]; then
		export DOCKER_IP="127.0.0.1"
	fi
fi

# docker-machine doesn't exist in Linux, assign default ip if it's not set
export DOCKER_IP=${DOCKER_IP:-172.17.0.1}
echo "Docker IP is $DOCKER_IP"

# Change directories
cd docker

# Display status of cluster
docker compose -f docker-compose.yml -f docker-compose-$suffix.yml logs $docker_image
