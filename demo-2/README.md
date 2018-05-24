# Partitions

This demo should be executed from the `environment/kafka-1.1.0` directory.

## Configure JAAS for Zookeeper authentication

```
export KAFKA_OPTS="-Djava.security.auth.login.config=../configs/kafka/jaas.config"
```

## Create topic

```
bin/kafka-topics.sh --zookeeper localhost:2181 --create --topic demo-2 --partitions 3 --replication-factor 1
```

## Check the created topic

```
bin/kafka-topics.sh --zookeeper localhost:2181 --list --topic demo-2
bin/kafka-topics.sh --zookeeper localhost:2181 --describe --topic demo-2
```

## Send some messages

```
bin/kafka-console-producer.sh --broker-list localhost:9092 --topic demo-2
```

## Consume messages

* Read from the whole topic

```
bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic demo-2 --from-beginning
```

* Notice how the messages are out of order. And check how nicely ordered they are in a single partition.

```
bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic demo-2 --partition 0 --from-beginning
```

* Show reading from a particular offset

```
bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic demo-2 --partition 0 --offset 2
```