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
public class AerospikeAppendTest {

  private static AerospikeClient aerospikeClient;
  private final Bin[] bins = {new Bin("bin1", "value1"), new Bin("bin2", "value2")};

  @BeforeAll
  public static void setup(Vertx vertx) {
    AerospikeConnectOptions connectOptions = new AerospikeConnectOptions()
        .setHost(System.getProperty(Constants.AEROSPIKE_HOST))
        .setPort(Integer.parseInt(System.getProperty(Constants.AEROSPIKE_PORT)));
    aerospikeClient = AerospikeClient.create(vertx, connectOptions);
  }

  @Test
  public void appendAllBins(VertxTestContext testContext) {
    Bin[] appendBins = {new Bin("bin1", "-append1"), new Bin("bin2", "-append2")};
    Key testKey = new Key(Constants.TEST_NAMESPACE, Constants.TEST_SET, "appendAllBins");
    aerospikeClient.rxPut(null, testKey, bins)
        .ignoreElement()
        .andThen(aerospikeClient.rxAppend(null, testKey, appendBins))
        .ignoreElement()
        .andThen(aerospikeClient.rxGet(null, testKey))
        .doOnSuccess(record -> {
          MatcherAssert.assertThat(record.getString("bin1"), Matchers.equalTo("value1-append1"));
          MatcherAssert.assertThat(record.getString("bin2"), Matchers.equalTo("value2-append2"));
        })
        .doOnSuccess(record -> log.info("appendAllBins test passed!"))
        .subscribe(record -> testContext.completeNow(), testContext::failNow);
  }

  @Test
  public void appendSomeBins(VertxTestContext testContext) {
    Bin[] appendBins = {new Bin("bin1", "-append1")};
    Key testKey = new Key(Constants.TEST_NAMESPACE, Constants.TEST_SET, "appendSomeBins");
    aerospikeClient.rxPut(null, testKey, bins)
        .ignoreElement()
        .andThen(aerospikeClient.rxAppend(null, testKey, appendBins))
        .ignoreElement()
        .andThen(aerospikeClient.rxGet(null, testKey))
        .doOnSuccess(record -> {
          MatcherAssert.assertThat(record.getString("bin1"), Matchers.equalTo("value1-append1"));
          MatcherAssert.assertThat(record.getString("bin2"), Matchers.equalTo("value2"));
        })
        .doOnSuccess(record -> log.info("appendSomeBins test passed!"))
        .subscribe(record -> testContext.completeNow(), testContext::failNow);
  }

  @Test
  public void appendNonExistingBin(VertxTestContext testContext) {
    Bin[] appendBins = {new Bin("bin1", "-append1"), new Bin("bin3", "value3")};
    Key testKey = new Key(Constants.TEST_NAMESPACE, Constants.TEST_SET, "appendNonExistingBin");
    aerospikeClient.rxPut(null, testKey, bins)
        .ignoreElement()
        .andThen(aerospikeClient.rxAppend(null, testKey, appendBins))
        .ignoreElement()
        .andThen(aerospikeClient.rxGet(null, testKey))
        .doOnSuccess(record -> {
          MatcherAssert.assertThat(record.getString("bin1"), Matchers.equalTo("value1-append1"));
          MatcherAssert.assertThat(record.getString("bin2"), Matchers.equalTo("value2"));
          MatcherAssert.assertThat(record.getString("bin3"), Matchers.equalTo("value3"));
        })
        .doOnSuccess(record -> log.info("appendNonExistingBin test passed!"))
        .subscribe(record -> testContext.completeNow(), testContext::failNow);
  }
}
