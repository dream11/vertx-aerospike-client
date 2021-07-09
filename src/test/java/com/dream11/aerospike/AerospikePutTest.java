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
        .setHost(System.getProperty(Constants.AEROSPIKE_HOST))
        .setPort(Integer.parseInt(System.getProperty(Constants.AEROSPIKE_PORT)));
    aerospikeClient = AerospikeClient.create(vertx, connectOptions);
  }

  @Test
  public void putAllBins(VertxTestContext testContext) {
    Bin[] bins = {new Bin("a", "aaa"), new Bin("b", 111)};
    Key testKey = new Key(Constants.TEST_NAMESPACE, Constants.TEST_SET, "zzz");
    aerospikeClient.rxPut(null, testKey, bins)
        .ignoreElement()
        .andThen(aerospikeClient.rxGet(null, testKey))
        .doOnSuccess(record -> {
          MatcherAssert.assertThat(record.getString("a"), Matchers.equalTo("aaa"));
          MatcherAssert.assertThat(record.getInt("b"), Matchers.equalTo(111));
        })
        .doOnSuccess(record -> log.info("putAllBins test passed!"))
        .subscribe(record -> testContext.completeNow(), testContext::failNow);
  }
}
