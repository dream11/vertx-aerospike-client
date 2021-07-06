package com.dream11.aerospike.factory;

import com.aerospike.client.Host;
import com.aerospike.client.policy.ClientPolicy;
import com.dream11.aerospike.client.AerospikeClient;
import com.dream11.aerospike.client.AerospikeClientImpl;
import com.dream11.aerospike.config.AerospikeConnectOptions;
import com.dream11.aerospike.util.PolicyUtil;
import com.dream11.aerospike.util.SharedDataUtils;
import io.vertx.core.*;
import lombok.extern.slf4j.Slf4j;


@Slf4j
public class AerospikeClientFactoryImpl implements AerospikeClientFactory {

  @Override
  public AerospikeClient getClient(Vertx vertx) {
    AerospikeConnectOptions config = new AerospikeConnectOptions();
    return getClient(vertx, config);
  }
  @Override
  public AerospikeClient getClient(Vertx vertx, AerospikeConnectOptions connectOptions) {
    String sharedInstanceName = "__AerospikeClient.__for.__" + connectOptions.getHost() + ":" + connectOptions.getPort();

    return SharedDataUtils.getOrCreate(new io.vertx.reactivex.core.Vertx(vertx), sharedInstanceName,
        () -> {
          PolicyUtil.setPolicies(connectOptions.getClientPolicy(), connectOptions);
          return getClientWithRetry(vertx, connectOptions);
        });
  }

  private AerospikeClient getClientWithRetry(Vertx vertx, AerospikeConnectOptions connectOptions) {
    try {
      return new AerospikeClientImpl(vertx, connectOptions.getClientPolicy(), new Host(connectOptions.getHost(), connectOptions.getPort()));
    } catch (Exception e) {
      log.error("Error while connecting to aerospike", e);
      log.info("retrying to connnect to aerospike");
      return getClientWithRetry(vertx, connectOptions);
    }
  }
}
