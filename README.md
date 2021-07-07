# The Reactive Aerospike Client

## Overview

TODO write overview of this repo

## Usage

Add the following dependency to the *dependencies* section of your build descriptor:
  
- Maven (in your `pom.xml`):
```xml
  <dependency>
    <groupId>io.vertx</groupId>
    <artifactId>vertx-aerospike-client</artifactId>
    <version>x.y.x</version>
  </dependency>
```

- Gradle (in your `build.gradle` file):
```
  dependencies {
   compile 'io.vertx:vertx-aerospike-client:x.y.z'
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
  AerospikeClient client = AerospikeClient.createShared(vertx, connectOptions);


  
```

## Configuration

Configuration options for `AerospikeConnectOptions`

TODO add all configuration options

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
    .map()...
```
