#!/usr/bin/env bash

set -e

if [ $# -ne 1 ]; then
    echo "Usage: ./provision.sh {new name for docker-machine instance}"
    exit 1
fi

name=$1


# Setup a Docker Machine instance
docker-machine create --driver virtualbox --virtualbox-cpu-count "2" --virtualbox-disk-size "40000" --virtualbox-memory "12288" $name
