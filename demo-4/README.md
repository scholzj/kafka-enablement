# Partitioning, consumer groups

This demo should be executed from the `environment/kafka-1.1.0` directory.

## Configure JAAS for Zookeeper authentication

```
export KAFKA_OPTS="-Djava.security.auth.login.config=../configs/kafka/jaas.config"
```

## Create topic

```
bin/kafka-topics.sh --zookeeper localhost:2181 --create --topic demo-4 --partitions 3 --replication-factor 1
```

## Setup consumers

* Open 3 consumers using the same group `group-1`

```
bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic demo-4 --from-beginning --property print.key=true --property key.separator=":" --group group-1
```

* Open consumer using a different group `group-2`

```
bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic demo-4 --from-beginning  --property print.key=true --property key.separator=":" --group group-2
```

## Send messages

* Send some messages with keys

```
bin/kafka-console-producer.sh --broker-list localhost:9092 --topic demo-4 --property "parse.key=true" --property "key.separator=:"
```

## Rebalancing consumer group

* Kill one of the consumers started before
* Send some messages with the same key as was used before for this consumer
* Show that one fo the other consumers got the partition assigned and will receive it
