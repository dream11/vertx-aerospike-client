package com.dream11.aerospike;

import com.aerospike.client.Bin;
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
public class AerospikePutTest {
  private static AerospikeClient aerospikeClient;

  @BeforeAll
  public static void setup(Vertx vertx) {
    AerospikeConnectOptions connectOptions = new AerospikeConnectOptions()
        .setHost(System.getProperty("aerospike.host"))
        .setPort(Integer.parseInt(System.getProperty("aerospike.port")));
    aerospikeClient = AerospikeClient.create(vertx, connectOptions);
  }

  @Test
  public void putAllBins(VertxTestContext testContext) {
    Bin[] bins = {new Bin("a", "aaa"), new Bin("b", 111)};
    Key testKey = new Key("test", "testset", "zzz");
    aerospikeClient.rxPut(null, testKey, bins)
        .doOnError(testContext::failNow)
        .subscribe();
    aerospikeClient.rxGet(null, testKey)
        .subscribe(record -> {
          MatcherAssert.assertThat(record.getString("a"), Matchers.equalTo("aaa"));
          MatcherAssert.assertThat(record.getInt("b"), Matchers.equalTo(111));
          log.info("putAllBins test passed!");
          testContext.completeNow();
        }, testContext::failNow);
  }
}
