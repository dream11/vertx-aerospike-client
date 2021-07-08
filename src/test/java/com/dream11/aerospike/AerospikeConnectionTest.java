package com.dream11.aerospike;

import com.dream11.aerospike.reactivex.client.AerospikeClient;
import com.dream11.aerospike.config.AerospikeConnectOptions;
import io.vertx.reactivex.core.Vertx;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith({VertxExtension.class, Setup.class})
@Slf4j
public class AerospikeConnectionTest {

  @BeforeAll
  public static void setup(Vertx vertx) {
    // Empty with vertx as parameter so that same vertx instance is used in all tests
  }

  @Test
  public void connect(Vertx vertx, VertxTestContext testContext) {
    AerospikeConnectOptions connectOptions = new AerospikeConnectOptions()
        .setHost(System.getProperty("aerospike.host"))
        .setPort(Integer.parseInt(System.getProperty("aerospike.port")));
    AerospikeClient.create(vertx, connectOptions);
    testContext.completeNow();
  }
}
