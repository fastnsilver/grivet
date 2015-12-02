#!/usr/bin/env bash

export SONAR_HOST=`docker-machine ip dev`

cd ../..
mvn clean verify 
mvn sonar:sonar \
  -Dsonar.host.url=http://$SONAR_HOST:9000 \
  -Dsonar.jdbc.url=jdbc:postgresql://$SONAR_HOST/sonar