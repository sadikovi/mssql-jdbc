#!/bin/bash

set -e

if [[ -z $(which docker) ]]; then
  echo "Docker is required"
  exit 1
fi

DOCKER_PID=$(docker run -e 'ACCEPT_EULA=Y' -e 'SA_PASSWORD=<YourStrong!Passw0rd>' -p 1433:1433 -d microsoft/mssql-server-linux:2017-latest)

while [[ -z $(docker ps | grep 'microsoft/mssql-server-linux:2017-latest') ]]; do
  echo "Waiting for the container to start..."
  sleep 1s
done

echo "Running container $DOCKER_PID"

export mssql_jdbc_test_connection_properties='jdbc:sqlserver://localhost:1433;databaseName=master;username=sa;password=<YourStrong!Passw0rd>;'
export mssql_jdbc_logging='true'
export mssql_jdbc_logging_handler="'console'|'file'"

mvn test || (docker stop $DOCKER_PID && docker rm $DOCKER_PID)
