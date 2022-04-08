package com.dream11.aerospike;

import com.aerospike.client.Bin;
import com.aerospike.client.Key;
import com.dream11.aerospike.client.AerospikeConnectOptions;
import com.dream11.reactivex.aerospike.client.AerospikeClient;
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
public class AerospikeExistsTest {
  private static final String testSet = "existsTestSet";
  private static final Key existingTestKey = new Key(Constants.TEST_NAMESPACE, testSet, "existingKey");
  private static final Key nonExistingTestKey = new Key(Constants.TEST_NAMESPACE, testSet, "nonExistingKey");
  private static final Bin[] bins = {new Bin("bin1", 8), new Bin("bin2", "value2")};
  private static AerospikeClient aerospikeClient;

  @BeforeAll
  public static void setup(Vertx vertx) {
    AerospikeConnectOptions connectOptions = new AerospikeConnectOptions()
        .setHost(System.getProperty(Constants.AEROSPIKE_HOST))
        .setPort(Integer.parseInt(System.getProperty(Constants.AEROSPIKE_PORT)));
    aerospikeClient = AerospikeClient.create(vertx, connectOptions);
    // add test keys
    aerospikeClient.getAerospikeClient().put(null, existingTestKey, bins);
  }

  @AfterAll
  public static void cleanUp() {
    // remove test keys
    aerospikeClient.getAerospikeClient().truncate(null, Constants.TEST_NAMESPACE, testSet, null);
    aerospikeClient.close();
  }

  @Test
  public void existingKey(VertxTestContext testContext) {
    aerospikeClient.rxExists(null, existingTestKey)
        .doOnSuccess(bool -> MatcherAssert.assertThat(bool, Matchers.equalTo(true)))
        .doOnSuccess(record -> log.info("existingKey test passed!"))
        .subscribe(record -> testContext.completeNow(), testContext::failNow);
  }

  @Test
  public void nonExistingKey(VertxTestContext testContext) {
    aerospikeClient.rxExists(null, nonExistingTestKey)
        .doOnSuccess(bool -> MatcherAssert.assertThat(bool, Matchers.equalTo(false)))
        .doOnSuccess(record -> log.info("nonExistingKey test passed!"))
        .subscribe(record -> testContext.completeNow(), testContext::failNow);
  }

  @Test
  public void checkMultipleKeys(VertxTestContext testContext) {
   aerospikeClient.rxExists(null, new Key[] {existingTestKey, nonExistingTestKey})
        .doOnSuccess(booleans -> {
          MatcherAssert.assertThat(booleans.get(0), Matchers.equalTo(true));
          MatcherAssert.assertThat(booleans.get(1), Matchers.equalTo(false));
        })
        .doOnSuccess(record -> log.info("checkMultipleKeys test passed!"))
        .subscribe(record -> testContext.completeNow(), testContext::failNow);
  }
}
