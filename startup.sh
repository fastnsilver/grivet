#!/usr/bin/env bash

set -e

if [ $# -ne 1 ]; then
    echo "Usage: ./startup.sh standalone|pipeline"
    exit 1
fi

suffix=$1

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

# Start the config service first and wait for it to become available
docker compose up -d config-service

while [ -z ${CONFIG_SERVICE_READY} ]; do
  echo "Waiting for config service..."
  if [ "$(curl --silent $DOCKER_IP:8888/actuator/health 2>&1 | grep -q '\"status\":\"UP\"'; echo $?)" = 0 ]; then
      CONFIG_SERVICE_READY=true;
  fi
  sleep 2
done

# Start the discovery service next and wait
docker compose up -d discovery-service

while [ -z ${DISCOVERY_SERVICE_READY} ]; do
  echo "Waiting for discovery service..."
  if [ "$(curl --silent $DOCKER_IP:8761/actuator/health 2>&1 | grep -q '\"status\":\"UP\"'; echo $?)" = 0 ]; then
      DISCOVERY_SERVICE_READY=true;
  fi
  sleep 2
done

# Start the other containers
docker compose -f docker-compose.yml -f docker-compose-$suffix.yml up -d

# Attach to the log output of the cluster
docker compose -f docker-compose.yml -f docker-compose-$suffix.yml logs

# Display status of cluster
docker compose -f docker-compose.yml -f docker-compose-$suffix.yml ps