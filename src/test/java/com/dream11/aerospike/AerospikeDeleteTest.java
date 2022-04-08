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
public class AerospikeDeleteTest {

  private static final String testSet = "deleteTestSet";
  private static final Bin[] bins = {new Bin("bin1", 8), new Bin("bin3", "value2")};
  private static final Key testKey = new Key(Constants.TEST_NAMESPACE, testSet, "delete");
  private static AerospikeClient aerospikeClient;

  @BeforeAll
  public static void setup(Vertx vertx) {
    AerospikeConnectOptions connectOptions = new AerospikeConnectOptions()
        .setHost(System.getProperty(Constants.AEROSPIKE_HOST))
        .setPort(Integer.parseInt(System.getProperty(Constants.AEROSPIKE_PORT)));
    aerospikeClient = AerospikeClient.create(vertx, connectOptions);
    aerospikeClient.getAerospikeClient().put(null, testKey, bins);
  }

  @AfterAll
  public static void cleanUp() {
    // remove test keys
    aerospikeClient.getAerospikeClient().truncate(null, Constants.TEST_NAMESPACE, testSet, null);
    aerospikeClient.close();
  }

  @Test
  public void delete(VertxTestContext testContext) {
    aerospikeClient.rxDelete(null, testKey)
        .ignoreElement()
        .andThen(aerospikeClient.rxExists(null, testKey))
        .doOnSuccess(bool -> MatcherAssert.assertThat(bool, Matchers.equalTo(false)))
        .doOnSuccess(record -> log.info("delete test passed!"))
        .subscribe(record -> testContext.completeNow(), testContext::failNow);
  }
}
