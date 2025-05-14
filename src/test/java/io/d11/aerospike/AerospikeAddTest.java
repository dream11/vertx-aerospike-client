package io.d11.aerospike;

import com.aerospike.client.Bin;
import com.aerospike.client.Key;
import io.d11.aerospike.client.AerospikeConnectOptions;
import io.d11.rxjava3.aerospike.client.AerospikeClient;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import io.vertx.rxjava3.core.Vertx;
import lombok.extern.slf4j.Slf4j;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith({VertxExtension.class, Setup.class})
@Slf4j
public class AerospikeAddTest {
  private static final String testSet = "appendTestSet";
  private static final Bin[] bins = {new Bin("bin1", 10), new Bin("bin2", 20)};
  private static final Key addAllBinsTestKey = new Key(Constants.TEST_NAMESPACE, testSet, "addAllBins");
  private static final Key addSomeBinsTestKey = new Key(Constants.TEST_NAMESPACE, testSet, "addSomeBins");
  private static final Key addNonExistingBinTestKey = new Key(Constants.TEST_NAMESPACE, testSet, "addNonExistingBin");
  private static AerospikeClient aerospikeClient;

  @BeforeAll
  public static void setup(Vertx vertx) {
    AerospikeConnectOptions connectOptions = new AerospikeConnectOptions()
        .setHost(System.getProperty(Constants.AEROSPIKE_HOST))
        .setPort(Integer.parseInt(System.getProperty(Constants.AEROSPIKE_PORT)));
    aerospikeClient = AerospikeClient.create(vertx, connectOptions);
    // add test keys
    aerospikeClient.getAerospikeClient().put(null, addAllBinsTestKey, bins);
    aerospikeClient.getAerospikeClient().put(null, addSomeBinsTestKey, bins);
    aerospikeClient.getAerospikeClient().put(null, addNonExistingBinTestKey, bins);
  }

  @AfterAll
  public static void cleanUp() {
    // remove test keys
    aerospikeClient.getAerospikeClient().truncate(null, Constants.TEST_NAMESPACE, testSet, null);
    aerospikeClient.close();
  }

  @Test
  public void addAllBins(VertxTestContext testContext) {
    Bin[] addBins = {new Bin("bin1", 5), new Bin("bin2", -5)};
    aerospikeClient.rxAdd(null, addAllBinsTestKey, addBins)
        .ignoreElement()
        .andThen(aerospikeClient.rxGet(null, addAllBinsTestKey))
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
    aerospikeClient.rxAdd(null, addSomeBinsTestKey, addBins)
        .ignoreElement()
        .andThen(aerospikeClient.rxGet(null, addSomeBinsTestKey))
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
    aerospikeClient.rxAdd(null, addNonExistingBinTestKey, addBins)
        .ignoreElement()
        .andThen(aerospikeClient.rxGet(null, addNonExistingBinTestKey))
        .doOnSuccess(record -> {
          MatcherAssert.assertThat(record.getInt("bin1"), Matchers.equalTo(18));
          MatcherAssert.assertThat(record.getInt("bin2"), Matchers.equalTo(20));
          MatcherAssert.assertThat(record.getInt("bin3"), Matchers.equalTo(-5));
        })
        .doOnSuccess(record -> log.info("addNonExistingBin test passed!"))
        .subscribe(record -> testContext.completeNow(), testContext::failNow);
  }
}
