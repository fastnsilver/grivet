#!/usr/bin/env bash

set -e

SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
directories=( "core/deployables/admin" "core/deployables/ingest-kafka" "core/deployables/ingest-rabbit" "core/deployables/persist-kafka" "core/deployables/persist-rabbit" "core/deployables/query" "core/deployables/standalone" "support/config-server" "support/api-gateway" "support/discovery-service" "support/microservices-console" )

for i in "${directories[@]}"
do
    if [ ! -f "$SCRIPT_DIR/$i/.dockerize" ]; then
        echo "Placing .dockerize file in $SCRIPT_DIR/$i";
        touch "$SCRIPT_DIR/$i/.dockerize";
    fi
done


# Build the project and docker images
echo "Building Grivet modules..."
mvn clean install -Dspring.cloud.compatibility-verifier.enabled=false
