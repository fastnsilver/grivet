#!/usr/bin/env bash

set -e

if [ $# -ne 1 ]; then
    echo "Usage: ./destroy.sh {existing name of a multipass instance}"
    exit 1
fi

name=$1


# Destroy a Multipass instance
if ! command -v multipass &> /dev/null
then
  multipass delete -p "$name"
else
  echo "Cannot execute destruction request, multipass is not installed!"
fi