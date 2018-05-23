#!/usr/bin/env bash

bin/kafka-topics.sh --zookeeper localhost:2181 --create --topic tweets --partitions 10 --replication-factor 1 --config retention.bytes=128000000 --config segment.bytes=16000000
bin/kafka-topics.sh --zookeeper localhost:2181 --create --topic users --partitions 10 --replication-factor 1 --config retention.bytes=128000000 --config segment.bytes=16000000
bin/kafka-topics.sh --zookeeper localhost:2181 --create --topic twitter-joined --partitions 1 --replication-factor 1
bin/kafka-topics.sh --zookeeper localhost:2181 --create --topic twitter-feed --partitions 1 --replication-factor 1
bin/kafka-topics.sh --zookeeper localhost:2181 --create --topic twitter-counter --partitions 1 --replication-factor 1
bin/kafka-topics.sh --zookeeper localhost:2181 --create --topic twitter-windowed-counter --partitions 3 --replication-factor 1
bin/kafka-topics.sh --zookeeper localhost:2181 --create --topic twitter-hashtag-counter --partitions 3 --replication-factor 1
bin/kafka-topics.sh --zookeeper localhost:2181 --create --topic twitter-windowed-hashtag-counter --partitions 3 --replication-factor 1