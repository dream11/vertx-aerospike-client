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
public class AerspikeDeleteTest {

  private static AerospikeClient aerospikeClient;

  @BeforeAll
  public static void setup(Vertx vertx) {
    AerospikeConnectOptions connectOptions = new AerospikeConnectOptions()
        .setHost(System.getProperty(Constants.AEROSPIKE_HOST))
        .setPort(Integer.parseInt(System.getProperty(Constants.AEROSPIKE_PORT)));
    aerospikeClient = AerospikeClient.create(vertx, connectOptions);
  }

  @Test
  public void delete(VertxTestContext testContext) {
    Bin[] bins = {new Bin("bin1", 8), new Bin("bin3", "value2")};
    Key testKey = new Key(Constants.TEST_NAMESPACE, Constants.TEST_SET, "delete");
    aerospikeClient.rxPut(null, testKey, bins)
        .ignoreElement()
        .andThen(aerospikeClient.rxDelete(null, testKey))
        .ignoreElement()
        .andThen(aerospikeClient.rxExists(null, testKey))
        .doOnSuccess(bool -> MatcherAssert.assertThat(bool, Matchers.equalTo(false)))
        .doOnSuccess(record -> log.info("delete test passed!"))
        .subscribe(record -> testContext.completeNow(), testContext::failNow);
  }
}
