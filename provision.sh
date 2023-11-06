#!/usr/bin/env bash

set -e

if [ $# -ne 1 ]; then
    echo "Usage: ./provision.sh {new name for multipass docker instance}"
    exit 1
fi

name=$1


# Setup a Multipass instance
if ! command -v multipass &> /dev/null
then
  multipass launch docker -c 2 -m 20G -d 40G -n "$name"
  multipass alias "$name":docker
else
  echo "Cannot execute provision request, multipass is not installed!"
fi