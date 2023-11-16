#!/usr/bin/env bash

# Orchestrates deployment of Grivet in pipeline mode

set -e

export MODE=grivet-pipeline
export REGISTRY_NAME=grivet-discovery-service
export QUEUE_NAME=grivet-queue-service

cf push -f core/deployables/admin/manifest.yml --no-route --no-start
cf push -f core/deployables/ingest/manifest.yml --no-route --no-start
cf push -f core/deployables/persist/manifest.yml --no-route --no-start
cf push -f core/deployables/query/manifest.yml --no-route --no-start

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
cf bind-service grivet-admin $MODE-config
cf bind-service grivet-ingest $MODE-config
cf bind-service grivet-persist $MODE-config
cf bind-service grivet-query $MODE-config

for (( i = 0; i < 90; i++ )); do
  if [[ $(cf service $REGISTRY_NAME) != *"succeeded"* ]]; then
    echo "$REGISTRY_NAME is not ready yet..."
    sleep 10s
  else
    break
  fi
done
cf bind-service grivet-admin $REGISTRY_NAME
cf bind-service grivet-ingest $REGISTRY_NAME
cf bind-service grivet-persist $REGISTRY_NAME
cf bind-service grivet-query $REGISTRY_NAME

for (( i = 0; i < 90; i++ )); do
  if [[ $(cf service $MODE-backend) != *"succeeded"* ]]; then
    echo "$MODE-backend is not ready yet..."
    sleep 10s
  else
    break
  fi
done
cf bind-service grivet-admin $MODE-backend
cf bind-service grivet-persist $MODE-backend
cf bind-service grivet-query $MODE-backend

for (( i = 0; i < 90; i++ )); do
  if [[ $(cf service $QUEUE_NAME) != *"succeeded"* ]]; then
    echo "$QUEUE_NAME is not ready yet..."
    sleep 10s
  else
    break
  fi
done
cf bind-service grivet-ingest $QUEUE_NAME
cf bind-service grivet-persist $QUEUE_NAME

cf start grivet-admin
cf start grivet-ingest
cf start grivet-persist
cf start grivet-query

cf push -f support/api-gateway/manifest.yml --no-start
cf bind-service grivet-api $MODE-config
cf bind-service grivet-api $REGISTRY_NAME
cf start grivet-api
