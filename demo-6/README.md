# Strimzi Cluster Controller

This demo should be executed from the `./` directory.

## Start OpenShift cluster

```
oc cluster up
oc login -u system:admin
```

or

```
minishift start
```

## Deploy Cluster Controller

* Show the deployment config and RBAC roles / bindings
* Install OpenShift templates

```
oc apply -f demo-6/strimzi/templates/ -n openshift
```

* Deploy the CC

```
oc apply -f demo-6/strimzi/install/
```

## Show CC running in OpenShift console

* Just do it

## Deploying cluster

* Show the templates
* Show the Config Maps
* Deploy Ephemeral cluster from a template

## Running cluster

* Show the running cluster
* Show some cluster changes

## Kafka Connect

* Deploy Kafka Connect S2I
* Trigger a new build with some plugins

```
oc start-build my-connect-cluster-connect --from-dir ./demo-6/plugins/
```

* Show how it builds and deploys