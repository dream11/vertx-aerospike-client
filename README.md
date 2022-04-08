# The Reactive Aerospike Client

[![Continuous Integration](https://github.com/dream11/vertx-aerospike-client/actions/workflows/ci.yml/badge.svg)](https://github.com/dream11/vertx-aerospike-client/actions/workflows/ci.yml)
[![Code Coverage](https://codecov.io/gh/dream11/vertx-aerospike-client/branch/master/graph/badge.svg)](https://codecov.io/gh/dream11/vertx-aerospike-client)
![License](https://img.shields.io/badge/license-MIT-green.svg)

## Overview

The Vert.x Aerospike client provides an asynchronous API to interact with aerospike server.
(Internally uses [AerospikeClient's](https://www.aerospike.com/docs/client/java/) async commands and handles the result on vertx-context)

## Usage

Add the following dependency to the *dependencies* section of your build descriptor:
  
- Maven (in your `pom.xml`):
```xml
  <dependency>
    <groupId>com.dream11</groupId>
    <artifactId>vertx-aerospike-client</artifactId>
    <version>LATEST</version>
  </dependency>
```

- Gradle (in your `build.gradle` file):
```
  dependencies {
   compile 'com.dream11:vertx-aerospike-client:x.y.z'
  }
```

## Connecting to Aerospike

```java
  AerospikeConnectOptions connectOptions = new AerospikeConnectOptions()
    .setHosts("my-host")
    .setEventLoopSize(16);

  // create a shared aerospike client across vertx instance
  AerospikeClient client = AerospikeClient.create(vertx, connectOptions);
  
  // create non shared aerospike client
  AerospikeClient client = AerospikeClient.createNonShared(vertx, connectOptions);
```

## Configuration

Configuration options for `AerospikeConnectOptions`

| Key | Default  | Type  | Required | Description |
| --- | --- | --- | --- | --- |
| host | localhost | String | false | Aerospike server host |
| port | 3000 | Integer | false | Aerospike server port |
| eventLoopSize | 2*<#cores> | Integer | false | Number of EventLoop threads |
| maxCommandsInProcess | 100 | Integer | false | Maximum number of commands in process on each EventLoop thread |
| maxCommandsInQueue | 0 | Integer | false | Maximum number of commands in each EventLoop's queue |
| maxConnsPerNode | 2*<#cores>*100 | Integer | false | Maximum number of connections to one server node |
| maxConnectRetries | 2 | Integer | false | Maximum number of retries to connect |
| clientPolicy | <ClientPolicy with replica policy MASTER_PROLES> | ClientPolicy | false | Aerospike client policy |

### Note on Configuration options:
* Do not set the clientPolicy.eventLoops. Use AerospikeConnectOptions to configure them.

## Running queries

```java
  AerospikeClient client = AerospikeClient.create(vertx, connectOptions);
  client
    .rxGet(policy, key)
    .map(record -> {
      // Handle record
        })...
```

Detailed documentation can be found [here](https://javadoc.io/doc/com.dream11/vertx-aerospike-client/latest/index.html).

## Running the tests

To run the test suite:
```shell
  mvn clean verify
```
The test suite runs a Docker container from image `aerospike/aerospike-server` using [TestContainers](https://www.testcontainers.org/)
by default. 

To run the test suite on a container built from a different docker image:
```shell
  mvn clean verify -Daerospike.image=<image>
```
