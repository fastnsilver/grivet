#!/usr/bin/env bash

set -e

if [ -z "$1" ]; then
    echo "Usage: ./status.sh standalone|pipeline"
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

# Display status of cluster
if [ -d "/tmp/signoz/deploy/docker/clickhouse-setup" ]; then
  docker compose -f /tmp/signoz/deploy/docker/clickhouse-setup/docker-compose.yaml ps -a
fi
docker compose -f docker-compose.yml -f docker-compose-"$suffix.yml" ps -a
