#!/usr/bin/env bash

set -e

# Stage the Maven Site locally
mvn site site:stage -Pdocumentation

# Publish to Github Pages
mvn scm-publish:publish-scm -Pdocumentation
