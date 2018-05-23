# Demo 8: Using AMQ Streams on OpenShift

In this demo we will install AMQ Stream On OpenShift Developer Preview 1 and deploy Kafka cluster.

## Preparation

* Login to the OpenShift cluster
* Create a new project

## Installing AMQ Streams

AMQ Streams follow the Kubernetes operator concept. Before deploying an actual Kafka cluster, we need to deploy the operator / controller.

* Create the service account

```
oc apply -f https://raw.githubusercontent.com/scholzj/kafka-enablement/master/demo-8/install/01-service-account.yaml
```

* Create role binding between the ServiceAccount and the `edit` ClusterRole

```
oc apply -f https://raw.githubusercontent.com/scholzj/kafka-enablement/master/demo-8/install/03-role-binding.yaml
```

* Deploy the Strimzi Cluster Controller

```
oc apply -f https://raw.githubusercontent.com/scholzj/kafka-enablement/master/demo-8/install/04-deployment.yaml
```

* Check that the controller is started and running

```
kubectl logs -l name=strimzi-cluster-controller
```

## Deploy Kafka cluster

Kafka cluster is deployed using a ConfigMap. The ConfigMap can be either created through the OpenShift template or by directly creating the ConfigMap.

* Check the ConfigMap in file `kafka-cluster.yaml`
* Deploy the ConfigMap to the cluster

```
oc apply -f https://raw.githubusercontent.com/scholzj/kafka-enablement/master/demo-8/kafka-cluster.yaml
```

* Watch as the Kafka cluster is deployed

```
oc get pods -w
```

## Updating cluster

Kafka clusters can be update by editing the ConfigMap.

* Edit the ConfigMap and increase the amount of Kafka nodes from 1 to 3 (set the field `kafka-nodes` to `3`). That can be done through the web console or using:

```
oc edit cm my-cluster
```

* Watch as the cluster is scaled up

```
oc get pods -w
```

* Edit the ConfigMap and set default number of replicas to 3. To do so, change the fields `KAFKA_DEFAULT_REPLICATION_FACTOR`,`KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR`and `KAFKA_TRANSACTION_STATE_LOG_REPLICATION_FACTOR` to `3`. That can be done through the web console or using:

```
oc edit cm my-cluster
```

* Watch the cluster go through the rolling update

```
oc get pods -w
```

## Topic Management

* Check the ConfigMap for defining a topic `kafka-topic.yaml`
* Deploy the ConfigMap to the cluster

```
oc apply -f https://raw.githubusercontent.com/scholzj/kafka-enablement/master/demo-8/kafka-topic.yaml
```

* Check in Kafka that the topic was created

```
oc apply -f https://raw.githubusercontent.com/scholzj/kafka-enablement/master/demo-8/kafka-topic.yaml
```

## Deploy application

* Deploy producer and consumer (Source code can be found here: [https://github.com/scholzj/kafka-test-apps](https://github.com/scholzj/kafka-test-apps))

```
oc apply -f https://raw.githubusercontent.com/scholzj/kafka-enablement/master/demo-8/kafka-clients.yaml
```

* Scale the consumer to 3 instances

```
oc scale deployment kafka-consumer --replicas=3
```

* Check how they split the load
* Scale the consumer to 4 instances

```
oc scale deployment kafka-consumer --replicas=3
```

* Check that one of them is idle