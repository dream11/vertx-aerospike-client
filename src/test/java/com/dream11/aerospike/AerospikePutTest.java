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
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith({VertxExtension.class, Setup.class})
@Slf4j
public class AerospikePutTest {
  private static final String testSet = "putTestSet";
  private static final Bin[] bins = {new Bin("bin1", "aaa"), new Bin("bin2", 111)};
  private static final Key testKey = new Key(Constants.TEST_NAMESPACE, testSet, "putAllBins");
  private static AerospikeClient aerospikeClient;

  @BeforeAll
  public static void setup(Vertx vertx) {
    AerospikeConnectOptions connectOptions = new AerospikeConnectOptions()
        .setHost(System.getProperty(Constants.AEROSPIKE_HOST))
        .setPort(Integer.parseInt(System.getProperty(Constants.AEROSPIKE_PORT)));
    aerospikeClient = AerospikeClient.create(vertx, connectOptions);
  }

  @AfterAll
  public static void cleanUp() {
    aerospikeClient.getAerospikeClient().delete(null, testKey);
    aerospikeClient.close();
  }

  @Test
  public void putAllBins(VertxTestContext testContext) {
    aerospikeClient.rxPut(null, testKey, bins)
        .ignoreElement()
        .andThen(aerospikeClient.rxGet(null, testKey))
        .doOnSuccess(record -> {
          MatcherAssert.assertThat(record.getString("bin1"), Matchers.equalTo("aaa"));
          MatcherAssert.assertThat(record.getInt("bin2"), Matchers.equalTo(111));
        })
        .doOnSuccess(record -> log.info("putAllBins test passed!"))
        .subscribe(record -> testContext.completeNow(), testContext::failNow);
  }
}
