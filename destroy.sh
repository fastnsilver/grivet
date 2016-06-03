#!/usr/bin/env bash

set -e

if [ $# -ne 1 ]; then
    echo "Usage: ./destroy.sh {existing name of a docker-machine instance}"
    exit 1
fi

name=$1


# Destroy a Docker Machine instance
docker-machine rm $name
