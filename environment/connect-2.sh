#!/usr/bin/env bash

source include.sh
KAFKA_DIR="${MY_DIR}/kafka-1.0.1"

${KAFKA_DIR}/bin/connect-distributed.sh ${CONNECT_CONFIG_DIR}/worker-2.properties
