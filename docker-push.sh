#!/usr/bin/env bash

# Publishes suite of pre-built container images to Dockerhub
set -e

CURRENT_DIR=$PWD
SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
directories=( "core/deployables/admin" "core/deployables/ingest-kafka" "core/deployables/ingest-rabbit" "core/deployables/persist-kafka" "core/deployables/persist-rabbit" "core/deployables/query" "core/deployables/standalone" "support/config-server" "support/api-gateway" "support/discovery-service" "support/microservices-console" )

for i in "${directories[@]}"
do
    if [ -f "$SCRIPT_DIR/$i/.dockerize" ]; then
        echo "Pushing image from $SCRIPT_DIR/$i";
        cd "$SCRIPT_DIR/$i"
        mvn docker:push
        cd $CURRENT_DIR
    fi
done

