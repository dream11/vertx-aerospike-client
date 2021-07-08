package com.dream11.aerospike;

import com.aerospike.client.Key;
import com.dream11.aerospike.config.AerospikeConnectOptions;
import com.dream11.aerospike.reactivex.client.AerospikeClient;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import io.vertx.reactivex.core.Vertx;
import lombok.extern.slf4j.Slf4j;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith({VertxExtension.class, Setup.class})
@Slf4j
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
  public void getAllBins(VertxTestContext testContext) {
    aerospikeClient.rxGet(null, new Key("test", "testset", "xyz"))
        .subscribe(record -> {
          MatcherAssert.assertThat(record.getString("a"), Matchers.equalTo("abc"));
          MatcherAssert.assertThat(record.getInt("b"), Matchers.equalTo(123));
          log.info("getAllBins test passed!");
          testContext.completeNow();
        }, testContext::failNow);
  }

  @Test
  public void getSelectedBins(VertxTestContext testContext) {
    aerospikeClient.rxGet(null, new Key("test", "testset", "xyz"), new String[] {"a"})
        .subscribe(record -> {
          MatcherAssert.assertThat(record.getString("a"), Matchers.equalTo("abc"));
          log.info("getSelectedBins test passed!");
          testContext.completeNow();
        }, testContext::failNow);
  }

}
