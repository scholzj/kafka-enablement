# Configuration

This demo should be executed from the `environment/kafka-1.1.0` directory.

## Configure JAAS for Zookeeper authentication

```
export KAFKA_OPTS="-Djava.security.auth.login.config=../configs/kafka/jaas.config"
```

## Zookeeper configuration

* Show Zookeeper config files
* Explain the `myid` file which needs to be created
* Show the ensemble configuration

## Kafka configuration

* Show Kafka configuration files
* Show `broker.id`
* Show listeners, advertised listeners, protocols
* Show Zookeeper config
* Show journal files
* Show other mgmt tools for reassigning topics etc.

## Show what Kafka does in Zookeeper

* Explain that ZK is integrated into the Kafka distro
* Start the ZK client

```
bin/zookeeper-shell.sh localhost:2181
```

* Show some paths in ZK

```
ls /
get /controller
ls /brokers
ls /brokers/ids
get /brokers/ids/0
ls /brokers/topics
```

* Show netcat dump with connected brokers

```
echo dump | nc localhost 2181
```

* Kill one of the brokers and show how it disappers

```
echo dump | nc localhost 2181
```

## Kafka Connect

* Show Kafka connect configuration files
* Show the plugin path and explain how it works
* Show basics of the rest interface

```
curl -s http://localhost:8083/ | jq
curl -s http://localhost:8083/connectors | jq
curl -s http://localhost:8083/connector-plugins | jq
```

## Docu

* Show Kafka website and docu