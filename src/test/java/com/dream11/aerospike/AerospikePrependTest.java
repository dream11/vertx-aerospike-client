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
public class AerospikePrependTest {

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
  public void prependAllBins(VertxTestContext testContext) {
    Bin[] prependBins = {new Bin("bin1", "prepend1-"), new Bin("bin2", "prepend2-")};
    Key testKey = new Key(Constants.TEST_NAMESPACE, Constants.TEST_SET, "prependAllBins");
    aerospikeClient.rxPut(null, testKey, bins)
        .ignoreElement()
        .andThen(aerospikeClient.rxPrepend(null, testKey, prependBins))
        .ignoreElement()
        .andThen(aerospikeClient.rxGet(null, testKey))
        .doOnSuccess(record -> {
          MatcherAssert.assertThat(record.getString("bin1"), Matchers.equalTo("prepend1-value1"));
          MatcherAssert.assertThat(record.getString("bin2"), Matchers.equalTo("prepend2-value2"));
        })
        .doOnSuccess(record -> log.info("prependAllBins test passed!"))
        .subscribe(record -> testContext.completeNow(), testContext::failNow);
  }

  @Test
  public void prependSomeBins(VertxTestContext testContext) {
    Bin[] prependBins = {new Bin("bin1", "prepend1-")};
    Key testKey = new Key(Constants.TEST_NAMESPACE, Constants.TEST_SET, "prependSomeBins");
    aerospikeClient.rxPut(null, testKey, bins)
        .ignoreElement()
        .andThen(aerospikeClient.rxPrepend(null, testKey, prependBins))
        .ignoreElement()
        .andThen(aerospikeClient.rxGet(null, testKey))
        .doOnSuccess(record -> {
          MatcherAssert.assertThat(record.getString("bin1"), Matchers.equalTo("prepend1-value1"));
          MatcherAssert.assertThat(record.getString("bin2"), Matchers.equalTo("value2"));
        })
        .doOnSuccess(record -> log.info("prependSomeBins test passed!"))
        .subscribe(record -> testContext.completeNow(), testContext::failNow);
  }

  @Test
  public void prependNonExistingBin(VertxTestContext testContext) {
    Bin[] prependBins = {new Bin("bin1", "prepend1-"), new Bin("bin3", "value3")};
    Key testKey = new Key(Constants.TEST_NAMESPACE, Constants.TEST_SET, "prependNonExistingBin");
    aerospikeClient.rxPut(null, testKey, bins)
        .ignoreElement()
        .andThen(aerospikeClient.rxPrepend(null, testKey, prependBins))
        .ignoreElement()
        .andThen(aerospikeClient.rxGet(null, testKey))
        .doOnSuccess(record -> {
          MatcherAssert.assertThat(record.getString("bin1"), Matchers.equalTo("prepend1-value1"));
          MatcherAssert.assertThat(record.getString("bin2"), Matchers.equalTo("value2"));
          MatcherAssert.assertThat(record.getString("bin3"), Matchers.equalTo("value3"));
        })
        .doOnSuccess(record -> log.info("prependNonExistingBin test passed!"))
        .subscribe(record -> testContext.completeNow(), testContext::failNow);
  }
}
