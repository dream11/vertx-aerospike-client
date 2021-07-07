package com.dream11.aerospike;

import com.aerospike.client.Key;
import com.dream11.aerospike.reactivex.client.AerospikeClient;
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
    io.vertx.reactivex.core.Vertx rxVertx = new io.vertx.reactivex.core.Vertx(vertx);
    AerospikeConnectOptions connectOptions = new AerospikeConnectOptions()
        .setHost(System.getProperty("aerospike.host"))
        .setPort(Integer.parseInt(System.getProperty("aerospike.port")));
    aerospikeClient = AerospikeClient.create(rxVertx, connectOptions);
  }

  @Test
  public void getAllBins(VertxTestContext testContext) {
    aerospikeClient.rxGet(null, new Key("test", "testset", "xyz"))
        .subscribe(record -> {
          MatcherAssert.assertThat(record.getString("a"), Matchers.equalTo("abc"));
          MatcherAssert.assertThat(record.getInt("b"), Matchers.equalTo(123));
          System.out.println("getAllBins test passed!");
          testContext.completeNow();
        }, testContext::failNow);
  }

  @Test
  public void getSelectedBins(VertxTestContext testContext) {
    aerospikeClient.rxGet(null, new Key("test", "testset", "xyz"), new String[]{"a"})
        .subscribe(record -> {
          MatcherAssert.assertThat(record.getString("a"), Matchers.equalTo("abc"));
          System.out.println("getSelectedBins test passed!");
          testContext.completeNow();
        }, testContext::failNow);
  }

}
