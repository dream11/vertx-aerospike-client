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
public class AerospikeAddTest {

  private static AerospikeClient aerospikeClient;
  private final Bin[] bins = {new Bin("bin1", 10), new Bin("bin2", 20)};

  @BeforeAll
  public static void setup(Vertx vertx) {
    AerospikeConnectOptions connectOptions = new AerospikeConnectOptions()
        .setHost(System.getProperty(Constants.AEROSPIKE_HOST))
        .setPort(Integer.parseInt(System.getProperty(Constants.AEROSPIKE_PORT)));
    aerospikeClient = AerospikeClient.create(vertx, connectOptions);
  }

  @Test
  public void addAllBins(VertxTestContext testContext) {
    Bin[] addBins = {new Bin("bin1", 5), new Bin("bin2", -5)};
    Key testKey = new Key(Constants.TEST_NAMESPACE, Constants.TEST_SET, "addAllBins");
    aerospikeClient.rxPut(null, testKey, bins)
        .ignoreElement()
        .andThen(aerospikeClient.rxAdd(null, testKey, addBins))
        .ignoreElement()
        .andThen(aerospikeClient.rxGet(null, testKey))
        .doOnSuccess(record -> {
          MatcherAssert.assertThat(record.getInt("bin1"), Matchers.equalTo(15));
          MatcherAssert.assertThat(record.getInt("bin2"), Matchers.equalTo(15));
        })
        .doOnSuccess(record -> log.info("addAllBins test passed!"))
        .subscribe(record -> testContext.completeNow(), testContext::failNow);
  }

  @Test
  public void addSomeBins(VertxTestContext testContext) {
    Bin[] addBins = {new Bin("bin1", 6)};
    Key testKey = new Key(Constants.TEST_NAMESPACE, Constants.TEST_SET, "addSomeBins");
    aerospikeClient.rxPut(null, testKey, bins)
        .ignoreElement()
        .andThen(aerospikeClient.rxAdd(null, testKey, addBins))
        .ignoreElement()
        .andThen(aerospikeClient.rxGet(null, testKey))
        .doOnSuccess(record -> {
          MatcherAssert.assertThat(record.getInt("bin1"), Matchers.equalTo(16));
          MatcherAssert.assertThat(record.getInt("bin2"), Matchers.equalTo(20));
        })
        .doOnSuccess(record -> log.info("addSomeBins test passed!"))
        .subscribe(record -> testContext.completeNow(), testContext::failNow);
  }

  @Test
  public void addNonExistingBin(VertxTestContext testContext) {
    Bin[] addBins = {new Bin("bin1", 8), new Bin("bin3", -5)};
    Key testKey = new Key(Constants.TEST_NAMESPACE, Constants.TEST_SET, "addNonExistingBin");
    aerospikeClient.rxPut(null, testKey, bins)
        .ignoreElement()
        .andThen(aerospikeClient.rxAdd(null, testKey, addBins))
        .ignoreElement()
        .andThen(aerospikeClient.rxGet(null, testKey))
        .doOnSuccess(record -> {
          MatcherAssert.assertThat(record.getInt("bin1"), Matchers.equalTo(18));
          MatcherAssert.assertThat(record.getInt("bin2"), Matchers.equalTo(20));
          MatcherAssert.assertThat(record.getInt("bin3"), Matchers.equalTo(-5));
        })
        .doOnSuccess(record -> log.info("addNonExistingBin test passed!"))
        .subscribe(record -> testContext.completeNow(), testContext::failNow);
  }
}
