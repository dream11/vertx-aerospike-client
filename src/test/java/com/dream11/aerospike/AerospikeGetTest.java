package com.dream11.aerospike;

import com.aerospike.client.Key;
import com.aerospike.client.policy.Policy;
import com.dream11.aerospike.client.AerospikeClient;
import com.dream11.aerospike.config.AerospikeConnectOptions;
import io.vertx.core.Vertx;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith({VertxExtension.class, Setup.class})
public class AerospikeGetTest {

  private static AerospikeClient aerospikeClient;

  @BeforeAll
  public static void setup(Vertx vertx) {
    AerospikeConnectOptions connectOptions = new AerospikeConnectOptions()
        .setHost(System.getProperty("aerospike.host"))
        .setPort(Integer.parseInt(System.getProperty("aerospike.port")));
    aerospikeClient = AerospikeClient.create(vertx, connectOptions);
  }

  @Test
  public void get(VertxTestContext testContext) {
    aerospikeClient.get(new Policy(), new Key("test", "testset", "xyz"), ar -> {
      if (ar.succeeded()) {
        MatcherAssert.assertThat(ar.result().getString("a"), Matchers.equalTo("abc"));
        MatcherAssert.assertThat(ar.result().getInt("b"), Matchers.equalTo(123));
        testContext.completeNow();
      } else {
        testContext.failNow(ar.cause());
      }
    });
  }
}
