package com.dream11.aerospike.factory;

import com.dream11.aerospike.client.AerospikeClient;
import com.dream11.aerospike.config.AerospikeConnectOptions;
import io.vertx.core.Vertx;

public interface AerospikeClientFactory {

  AerospikeClient getClient(Vertx vertx);

  AerospikeClient getClient(Vertx vertx, AerospikeConnectOptions config);
}
