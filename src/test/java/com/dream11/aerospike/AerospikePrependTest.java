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
public class AerospikePrependTest {
  private static final String testSet = "prependTestSet";
  private static final Bin[] bins = {new Bin("bin1", "value1"), new Bin("bin2", "value2")};
  private static final Key prependAllBinsTestKey = new Key(Constants.TEST_NAMESPACE, testSet, "appendAllBins");
  private static final Key prependSomeBinsTestKey = new Key(Constants.TEST_NAMESPACE, testSet, "appendSomeBins");
  private static final Key prependNonExistingBinTestKey = new Key(Constants.TEST_NAMESPACE, testSet, "appendNonExistingBin");
  private static AerospikeClient aerospikeClient;

  @BeforeAll
  public static void setup(Vertx vertx) {
    AerospikeConnectOptions connectOptions = new AerospikeConnectOptions()
        .setHost(System.getProperty(Constants.AEROSPIKE_HOST))
        .setPort(Integer.parseInt(System.getProperty(Constants.AEROSPIKE_PORT)));
    aerospikeClient = AerospikeClient.create(vertx, connectOptions);
    // add test keys
    aerospikeClient.getAerospikeClient().put(null, prependAllBinsTestKey, bins);
    aerospikeClient.getAerospikeClient().put(null, prependSomeBinsTestKey, bins);
    aerospikeClient.getAerospikeClient().put(null, prependNonExistingBinTestKey, bins);
  }

  @AfterAll
  public static void cleanUp() {
    // remove test keys
    aerospikeClient.getAerospikeClient().truncate(null, Constants.TEST_NAMESPACE, testSet, null);
    aerospikeClient.close();
  }

  @Test
  public void prependAllBins(VertxTestContext testContext) {
    Bin[] prependBins = {new Bin("bin1", "prepend1-"), new Bin("bin2", "prepend2-")};
    aerospikeClient.rxPrepend(null, prependAllBinsTestKey, prependBins)
        .ignoreElement()
        .andThen(aerospikeClient.rxGet(null, prependAllBinsTestKey))
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
    aerospikeClient.rxPrepend(null, prependSomeBinsTestKey, prependBins)
        .ignoreElement()
        .andThen(aerospikeClient.rxGet(null, prependSomeBinsTestKey))
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
    aerospikeClient.rxPrepend(null, prependNonExistingBinTestKey, prependBins)
        .ignoreElement()
        .andThen(aerospikeClient.rxGet(null, prependNonExistingBinTestKey))
        .doOnSuccess(record -> {
          MatcherAssert.assertThat(record.getString("bin1"), Matchers.equalTo("prepend1-value1"));
          MatcherAssert.assertThat(record.getString("bin2"), Matchers.equalTo("value2"));
          MatcherAssert.assertThat(record.getString("bin3"), Matchers.equalTo("value3"));
        })
        .doOnSuccess(record -> log.info("prependNonExistingBin test passed!"))
        .subscribe(record -> testContext.completeNow(), testContext::failNow);
  }
}
