#!/usr/bin/env bash

# Orchestrates deployment of Grivet in pipeline mode

set -e

if [ -z "$1" ]; then
    echo "Usage: ./cf-push-pipeline.sh kafka|rabbit"
    exit 1
fi

QUEUE_PROVIDER=$1

export MODE=grivet-pipeline
export REGISTRY_NAME=grivet-discovery-service
export QUEUE_NAME=grivet-queue-service

cf push -f core/deployables/admin/manifest.yml --no-start
cf push -f core/deployables/ingest-$QUEUE_PROVIDER/manifest.yml --no-start
cf push -f core/deployables/persist-$QUEUE_PROVIDER/manifest.yml --no-start
cf push -f core/deployables/query/manifest.yml --no-start

cf create-service p.config-server standard $MODE-config -c config-repo/config-server.json
cf create-service p.service-registry standard $REGISTRY_NAME
cf create-service p.mysql db-small-80 $MODE-backend
cf create-service p.rabbitmq single-node $QUEUE_NAME

for (( i = 0; i < 90; i++ )); do
  if [[ $(cf service $MODE-config) != *"succeeded"* ]]; then
    echo "$MODE-config is not ready yet..."
    sleep 10s
  else
    break
  fi
done

for (( i = 0; i < 90; i++ )); do
  if [[ $(cf service $REGISTRY_NAME) != *"succeeded"* ]]; then
    echo "$REGISTRY_NAME is not ready yet..."
    sleep 10s
  else
    break
  fi
done

for (( i = 0; i < 90; i++ )); do
  if [[ $(cf service $MODE-backend) != *"succeeded"* ]]; then
    echo "$MODE-backend is not ready yet..."
    sleep 10s
  else
    break
  fi
done

for (( i = 0; i < 90; i++ )); do
  if [[ $(cf service $QUEUE_NAME) != *"succeeded"* ]]; then
    echo "$QUEUE_NAME is not ready yet..."
    sleep 10s
  else
    break
  fi
done

cf start grivet-admin
cf start grivet-ingest
cf start grivet-persist
cf start grivet-query

cf push -f support/api-gateway/manifest.yml
