package com.dream11.aerospike.factory;

import com.aerospike.client.policy.ClientPolicy;
import com.dream11.aerospike.client.AerospikeClient;
import com.dream11.aerospike.config.AerospikeConnectOptions;
import io.vertx.core.Vertx;

public interface AerospikeClientFactory {

  AerospikeClient getClient(Vertx vertx);

  AerospikeClient getClient(Vertx vertx, AerospikeConnectOptions config);

  AerospikeClient getClient(Vertx vertx, ClientPolicy clientPolicy, AerospikeConnectOptions config);
}
