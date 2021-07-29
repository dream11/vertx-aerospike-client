package com.dream11.aerospike;

import com.dream11.aerospike.config.AerospikeConnectOptions;
import com.dream11.aerospike.reactivex.client.AerospikeClient;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import io.vertx.reactivex.core.Vertx;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith({VertxExtension.class, Setup.class})
@Slf4j
public class AerospikeConnectionTest {

  private static AerospikeClient aerospikeClient;

  @BeforeAll
  public static void setup(Vertx vertx) {
    // Empty with vertx as parameter so that same vertx instance is used in all tests
  }

  @AfterAll
  public static void cleanUp() {
    // remove test keys
    aerospikeClient.close();
  }

  @Test
  public void connect(Vertx vertx, VertxTestContext testContext) {
    AerospikeConnectOptions connectOptions = new AerospikeConnectOptions()
        .setHost(System.getProperty(Constants.AEROSPIKE_HOST))
        .setPort(Integer.parseInt(System.getProperty(Constants.AEROSPIKE_PORT)));
    aerospikeClient = AerospikeClient.create(vertx, connectOptions);
    testContext.completeNow();
  }
}
