# Replicas

This demo should be executed from the `environment/kafka-1.1.0` directory.

## Configure JAAS for Zookeeper authentication

```
export KAFKA_OPTS="-Djava.security.auth.login.config=../configs/kafka/jaas.config"
```

## Create topic

The topic should have 3 partitions and 3 replicas

```
bin/kafka-topics.sh --zookeeper localhost:2181 --create --topic demo-3 --partitions 3 --replication-factor 3
```

## Check the created topic

```
bin/kafka-topics.sh --zookeeper localhost:2181 --describe --topic demo-3
```

Notice the distribution of leaders and the ISR replicas. Explain also the RackID feature.

## Send some messages

```
bin/kafka-verifiable-producer.sh --broker-list localhost:9092 --topic demo-3 --max-messages 20
```

## Consume messages

* Show that the messages are still in the topic!

```
bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic demo-3 --from-beginning
```

## Broker crash

* Kill one of the brokers
* Show again the topic description with the leaders which changed and new ISR

```
bin/kafka-topics.sh --zookeeper localhost:2181 --describe --topic demo-3
```

## Consume messages

* Show that the messages are still in the topic!

```
bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic demo-3 --from-beginning
```

## Start the broker again

* Leadership didn't changed, but all replicas are again ISR