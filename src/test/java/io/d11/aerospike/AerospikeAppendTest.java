package io.d11.aerospike;

import com.aerospike.client.Bin;
import com.aerospike.client.Key;
import io.d11.aerospike.client.AerospikeConnectOptions;
import io.d11.reactivex.aerospike.client.AerospikeClient;
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
public class AerospikeAppendTest {

  private static final String testSet = "appendTestSet";
  private static final Bin[] bins = {new Bin("bin1", "value1"), new Bin("bin2", "value2")};
  private static final Key appendAllBinsTestKey = new Key(Constants.TEST_NAMESPACE, testSet, "appendAllBins");
  private static final Key appendSomeBinsTestKey = new Key(Constants.TEST_NAMESPACE, testSet, "appendSomeBins");
  private static final Key appendNonExistingBinTestKey = new Key(Constants.TEST_NAMESPACE, testSet, "appendNonExistingBin");
  private static AerospikeClient aerospikeClient;

  @BeforeAll
  public static void setup(Vertx vertx) {
    AerospikeConnectOptions connectOptions = new AerospikeConnectOptions()
        .setHost(System.getProperty(Constants.AEROSPIKE_HOST))
        .setPort(Integer.parseInt(System.getProperty(Constants.AEROSPIKE_PORT)));
    aerospikeClient = AerospikeClient.create(vertx, connectOptions);
    // add test keys
    aerospikeClient.getAerospikeClient().put(null, appendAllBinsTestKey, bins);
    aerospikeClient.getAerospikeClient().put(null, appendSomeBinsTestKey, bins);
    aerospikeClient.getAerospikeClient().put(null, appendNonExistingBinTestKey, bins);
  }

  @AfterAll
  public static void cleanUp() {
    // remove test keys
    aerospikeClient.getAerospikeClient().truncate(null, Constants.TEST_NAMESPACE, testSet, null);
    aerospikeClient.close();
  }

  @Test
  public void appendAllBins(VertxTestContext testContext) {
    Bin[] appendBins = {new Bin("bin1", "-append1"), new Bin("bin2", "-append2")};
    aerospikeClient.rxAppend(null, appendAllBinsTestKey, appendBins)
        .ignoreElement()
        .andThen(aerospikeClient.rxGet(null, appendAllBinsTestKey))
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
    aerospikeClient.rxAppend(null, appendSomeBinsTestKey, appendBins)
        .ignoreElement()
        .andThen(aerospikeClient.rxGet(null, appendSomeBinsTestKey))
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
    aerospikeClient.rxAppend(null, appendNonExistingBinTestKey, appendBins)
        .ignoreElement()
        .andThen(aerospikeClient.rxGet(null, appendNonExistingBinTestKey))
        .doOnSuccess(record -> {
          MatcherAssert.assertThat(record.getString("bin1"), Matchers.equalTo("value1-append1"));
          MatcherAssert.assertThat(record.getString("bin2"), Matchers.equalTo("value2"));
          MatcherAssert.assertThat(record.getString("bin3"), Matchers.equalTo("value3"));
        })
        .doOnSuccess(record -> log.info("appendNonExistingBin test passed!"))
        .subscribe(record -> testContext.completeNow(), testContext::failNow);
  }
}
