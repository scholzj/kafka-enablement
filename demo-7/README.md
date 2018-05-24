# Topic Operator

This demo should be executed from the `./` directory.

## Show how Topic Operator deployed with cluster

* In console

## Show Topic Operator in action

* Create topic through Kafka

```
oc exec my-cluster-kafka-1 -i -t -- bin/kafka-topics.sh --zookeeper my-cluster-zookeeper:2181 --create --topic created-in-kafka --partitions 3 --replication-factor 1 --config cleanup.policy=compact
```

* Create topic through Config Map

```
oc apply -f demo-7/topic.yaml
```

* Show how they are reconciled

```
oc exec my-cluster-kafka-1 -i -t -- bin/kafka-topics.sh --zookeeper my-cluster-zookeeper:2181 --describe --topic created-as-configmap
oc get configmap created-in-kafka -o yaml
```

## Application

* Deploy the example app

```
oc apply -f demo-7/example-app.yaml
```

* Show the app running and communicating