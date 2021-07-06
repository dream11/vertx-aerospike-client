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

  AerospikeClient client = AerospikeClient.create(vertx, connectOptions);
  
```

## Configuration

Configuration options for `AerospikeConnectOptions`

TODO add all configuration options

| Key | Default  | Type  | Required | Description |
| --- | --- | --- | --- | --- |
| host | localhost | String | false | Aerospike server host |

## Running queries

```java
  AerospikeClient client = AerospikeClient.create(vertx, connectOptions);
  client
    .rxGet(policy, key)
    .map()...
```
