#!/usr/bin/env bash

set -e

if [ -z "$1" ]; then
    echo "Usage: ./show-log.sh standalone|pipeline {service_name}"
    exit 1
fi

suffix=$1
service_name=${2:-}

export DOCKER_IP="host.docker.internal"

os=$(uname)
if [[ "$os" == *"Linux"* ]]; then
  export DOCKER_IP="172.17.0.1"
fi

# Change directories
cd docker

# Display logs for service in cluster
if [ -d "/tmp/signoz/deploy/docker/clickhouse-setup" ] && [ "$suffix" == "infra" ]; then
  docker compose -f /tmp/signoz/deploy/docker/clickhouse-setup/docker-compose.yaml logs $service_name
else
  docker compose -f docker-compose.yml -f docker-compose-"$suffix.yml" logs $service_name
fi