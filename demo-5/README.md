# Twitter analytics using Apache Kafka

Scripts should be executed from the `environment/kafka-1.1.0` directory. Java code can run from your IDE.

## Configure JAAS for Zookeeper authentication

```
export KAFKA_OPTS="-Djava.security.auth.login.config=../configs/kafka/jaas.config"
```

## Create topics

```
../../demo-5/create-topics.sh
```

or

```
bin/kafka-topics.sh --zookeeper localhost:2181 --create --topic tweets --partitions 10 --replication-factor 1 --config retention.bytes=128000000 --config segment.bytes=16000000
bin/kafka-topics.sh --zookeeper localhost:2181 --create --topic users --partitions 10 --replication-factor 1 --config retention.bytes=128000000 --config segment.bytes=16000000
bin/kafka-topics.sh --zookeeper localhost:2181 --create --topic twitter-feed --partitions 1 --replication-factor 1
bin/kafka-topics.sh --zookeeper localhost:2181 --create --topic twitter-joined --partitions 1 --replication-factor 1
bin/kafka-topics.sh --zookeeper localhost:2181 --create --topic twitter-counter --partitions 1 --replication-factor 1
bin/kafka-topics.sh --zookeeper localhost:2181 --create --topic twitter-windowed-counter --partitions 3 --replication-factor 1
bin/kafka-topics.sh --zookeeper localhost:2181 --create --topic twitter-hashtag-counter --partitions 3 --replication-factor 1
bin/kafka-topics.sh --zookeeper localhost:2181 --create --topic twitter-windowed-hashtag-counter --partitions 3 --replication-factor 1
```

## Deploy Twitter Connector

* Deploy it through Kafka Connect REST API

```
curl -X POST -H "Content-Type: application/json" --data @../../demo-5/connector-with-credentials.json http://localhost:8083/connectors
```

* Check status

```
curl http://localhost:8083/connectors/twitter-feed | jq
curl http://localhost:8083/connectors/twitter-feed/status | jq
curl http://localhost:8083/connectors/twitter-feed/tasks/0/status | jq
```

* Check that tweets are flowing in

```
bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic twitter-feed --from-beginning
```

* Show the example tweet to details the layout (tweet-sample.json)

## Kafka Streams examples

* Run the transformer demo
* Show results

```
bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic tweets  --property print.key=true --property key.separator=": "
```

* Run the Join example
* Show the joined tweets

```
bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic twitter-joined
```

* Run the tweet counter example
* Check the results
 
```
bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 --value-deserializer org.apache.kafka.common.serialization.LongDeserializer --topic twitter-counter --from-beginning
```

* Run the windowed tweet counter example
* Check the results
 
```
bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 --value-deserializer org.apache.kafka.common.serialization.LongDeserializer --topic twitter-windowed-counter --from-beginning
```

* Run the hashtag counter example
* Check the results
 
```
bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 --property print.key=true  --value-deserializer org.apache.kafka.common.serialization.LongDeserializer --topic twitter-hashtag-counter --from-beginning
```

* Run the windowed hashtag counter example
* Check the results

```
bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 --property print.key=true  --value-deserializer org.apache.kafka.common.serialization.LongDeserializer --topic twitter-windowed-hashtag-counter --from-beginning
```
