#!/usr/bin/env bash

set -e

if [ -z "$1" ]; then
    echo "Usage: ./startup.sh standalone|pipeline"
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

# Fetch and start ancillary services
cd /tmp
if [ ! -d "signoz" ]; then
  git clone https://github.com/SigNoz/signoz.git
fi
cd signoz/deploy
docker compose -f docker/clickhouse-setup/docker-compose.yaml up -d
cd ../..

cd "$CURRENT_DIR"

# Start the config service first and wait for it to become available
docker compose up -d config-service

while [ -z "$CONFIG_SERVICE_READY" ]; do
  echo "Waiting for config service..."
  if [ "$(curl --silent "$DOCKER_IP":8888/actuator/health 2>&1 | grep -q '\"status\":\"UP\"'; echo $?)" = 0 ]; then
      CONFIG_SERVICE_READY=true;
  fi
  sleep 5
done

# Start the discovery service next and wait
docker compose up -d discovery-service

while [ -z "$DISCOVERY_SERVICE_READY" ]; do
  echo "Waiting for discovery service..."
  if [ "$(curl --silent "$DOCKER_IP":8761/actuator/health 2>&1 | grep -q '\"status\":\"UP\"'; echo $?)" = 0 ]; then
      DISCOVERY_SERVICE_READY=true;
  fi
  sleep 5
done

# Start the other containers
docker compose -f docker-compose.yml -f docker-compose-"$suffix.yml" up -d

cd ..

# Attach to the log output of the cluster
./show-log.sh "$suffix"

# Display status of cluster
./status.sh "$suffix"