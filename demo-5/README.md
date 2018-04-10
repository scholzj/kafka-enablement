# Twitter analytics using Apache Kafka

## Start OpenShift

* Start OpenShift
```
oc cluster up
```

* Login as admin
```
oc login -u system:admin
```

## Install Strimzi

* Install Cluster controller
```
oc apply -f strimzi-0.2.0/examples/install/cluster-controller/
```

## Deploy Kafka and Kafka Connect clusters

* Install Kafka
```
oc apply -f clusters/kafka-persistent.yaml
```

* Install Kafka
```
oc apply -f clusters/kafka-connect.yaml
```  

## Add Twitter Connector

* Start new OpenShift build to add the connectors
```
oc start-build my-connect-cluster-connect --from-dir ./kafka-connect-plugins/
```

## Create topics

* Create topics using Topic Controller and Config Maps
```
oc apply -f ./topics/
``` 

* Show the topics
```
./bin/kafka-topics.sh  --zookeeper my-cluster-zookeeper:2181 --describe
```

## Deploy Twitter Connector

* Deploy it through Kafka Connect REST API
```
curl -X POST -H "Content-Type: application/json" --data @connector-with-credentials.json http://localhost:8083/connectors
```

* Check status
```
curl http://172.30.70.185:8083/connectors/twitter-feed/status | jq
curl http://172.30.70.185:8083/connectors/twitter-feed/tasks/0/status | jq
```

* Check that tweets are flowing in
```
bin/kafka-console-consumer.sh --bootstrap-server my-cluster-kafka:9092 --topic twitter-feed --from-beginning
```

## Kafka Streams examples

* Run the transformer demo
* Show results
```
kafkacat -C -t tweets -b 172.30.53.23:9092 -f "%k: %s\n\n"
```

* Run the hashtag counter example
* Check the results
```
bin/kafka-console-consumer.sh --bootstrap-server my-cluster-kafka:9092 --property print.key=true  --value-deserializer org.apache.kafka.common.serialization.LongDeserializer --topic twitter-hashtag-counter --from-beginning
```

* Run the windowed hashtag counter example
* Check the results
```
bin/kafka-console-consumer.sh --bootstrap-server my-cluster-kafka:9092 --property print.key=true  --value-deserializer org.apache.kafka.common.serialization.LongDeserializer --topic twitter-windowed-hashtag-counter --from-beginning
```

