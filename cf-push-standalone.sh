#!/usr/bin/env bash

# Orchestrates deployment of Grivet in standalone mode

set -e

export APP_NAME=grivet-standalone

cf push -f core/deployables/standalone/manifest.yml --no-start

cf create-service p.config-server standard $APP_NAME-config -c config-repo/config-server.json
cf create-service p.mysql db-small-80 $APP_NAME-backend

while [[ $(cf service $APP_NAME-config) != *"succeeded"* ]]; do
  echo "$APP_NAME-config is not ready yet..."
  sleep 5s
done
cf bind-service $APP_NAME $APP_NAME-config

while [[ $(cf service $APP_NAME-backend) != *"succeeded"* ]]; do
    echo "$APP_NAME-backend is not ready yet..."
    sleep 5s
done
cf bind-service $APP_NAME $APP_NAME-backend

cf start $APP_NAME
