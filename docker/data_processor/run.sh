#!/bin/bash
echo "==========> sleeping 30 seconds for mysql to initialize"
sleep 30

export BASE_SERVER_JVM_ARGS="${BASE_SERVER_JVM_ARGS}"

COMMAND="java $BASE_SERVER_JVM_ARGS \
-jar ${APP_DIR}/data-processor.jar"

echo "==========> $COMMAND"

exec ${COMMAND}