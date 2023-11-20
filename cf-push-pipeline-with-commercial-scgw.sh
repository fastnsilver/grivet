#!/usr/bin/env bash

# Orchestrates deployment of Grivet in pipeline mode targeting Tanzu Application Service using the Spring Cloud Gateway tile
set -e

QUEUE_PROVIDER=rabbit

export MODE=grivet-pipeline
export REGISTRY_NAME=grivet-discovery-service
export QUEUE_NAME=grivet-queue-service
export GW_NAME=grivet-api

cf push -f core/deployables/admin/manifest.yml --no-route --no-start
cf push -f core/deployables/ingest-$QUEUE_PROVIDER/manifest.yml --no-route --no-start
cf push -f core/deployables/persist-$QUEUE_PROVIDER/manifest.yml --no-route --no-start
cf push -f core/deployables/query/manifest.yml --no-route --no-start

cf map-route grivet-admin apps.internal --hostname grivet-admin
cf map-route grivet-ingest apps.internal --hostname grivet-ingest
cf map-route grivet-persist apps.internal --hostname grivet-persist
cf map-route grivet-query apps.internal --hostname grivet-query

cf create-service p.config-server standard $MODE-config -c config-repo/config-server.json
cf create-service p.service-registry standard $REGISTRY_NAME
cf create-service p.mysql db-small-80 $MODE-backend
cf create-service p.rabbitmq single-node $QUEUE_NAME
cf create-service p.gateway standard $GW_NAME

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

for (( i = 0; i < 90; i++ )); do
  if [[ $(cf service $GW_NAME) != *"succeeded"* ]]; then
    echo "$GW_NAME is not ready yet..."
    sleep 10s
  else
    break
  fi
done

cf start grivet-admin
cf start grivet-ingest
cf start grivet-persist
cf start grivet-query

# @see https://docs.vmware.com/en/Spring-Cloud-Gateway-for-VMware-Tanzu/2.1/spring-cloud-gateway/GUID-guides-configuring-routes.html
cf bind-service grivet-admin $GW_NAME -c '{ "routes": [ { "path": "api/v1/definition/**,/api/v1/definitions/**,/api/v1/schema", "uri": "lb://grivet-admin.apps.internal", "filters": [ "StripPrefix=2" ] } ] }'
cf bind-service grivet-ingest $GW_NAME -c '{ "routes": [ { "path": "/api/v1/type/**,/api/v1/types", "method": "POST,PATCH,DELETE", "uri": "lb://grivet-ingest.apps.internal", "filters": [ "StripPrefix=2" ] } ] }'
cf bind-service grivet-persist $GW_NAME -c '{ "routes": [ { "path": "/api/v1/type/**,/api/v1/types", "method": "GET", "uri": "lb://grivet-persist.apps.internal", "filters": [ "StripPrefix=2" ] } ] }'
cf bind-service grivet-query $GW_NAME -c '{ "routes": [ { "path": "/api/v1/query/**,/api/v1/queries", "uri": "lb://grivet-query.apps.internal", "filters": [ "StripPrefix=2" ] } ] }'
