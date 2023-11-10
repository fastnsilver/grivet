#!/usr/bin/env bash

set -e

if [ -z "$1" ]; then
    echo "Usage: ./shutdown.sh standalone|pipeline"
    exit 1
fi

suffix=$1

export DOCKER_IP="host.docker.internal"

os=$(uname)
if [[ "$os" == *"Linux"* ]]; then
  export DOCKER_IP="172.17.0.1"
fi

# Change directories
cd docker
CURRENT_DIR="$PWD"

# Shutdown ancillary services
cd /tmp
if [ -d "signoz" ]; then
  cd signoz/deploy
  docker compose -f docker/clickhouse-setup/docker-compose.yaml down
  cd ../..
fi

cd "$CURRENT_DIR"

# Remove existing containers
docker compose -f docker-compose.yml -f docker-compose-"$suffix.yml" down

# Additional cleanup
docker image prune -f
docker volume prune -f
docker network prune -f
