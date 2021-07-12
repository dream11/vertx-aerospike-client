package com.dream11.aerospike;

import com.aerospike.client.Bin;
import com.aerospike.client.Key;
import com.aerospike.client.policy.WritePolicy;
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
public class AerospikeGetHeaderTest {

  private static AerospikeClient aerospikeClient;
  private static final Key key3 = new Key(Constants.TEST_NAMESPACE, Constants.TEST_SET, "pkey3");
  private static final Key key4 = new Key(Constants.TEST_NAMESPACE, Constants.TEST_SET, "pkey4");

  @BeforeAll
  public static void setup(Vertx vertx) {
    AerospikeConnectOptions connectOptions = new AerospikeConnectOptions()
        .setHost(System.getProperty(Constants.AEROSPIKE_HOST))
        .setPort(Integer.parseInt(System.getProperty(Constants.AEROSPIKE_PORT)));
    aerospikeClient = AerospikeClient.create(vertx, connectOptions);

    WritePolicy policy = new WritePolicy();
    policy.expiration = -1;
    Bin[] bins3 = {new Bin("bin1", "Bangalore"), new Bin("bin2", "235")};
    Bin[] bins4 = {new Bin("bin1", "Hyderabad"), new Bin("bin2", "246")};
    aerospikeClient.getAerospikeClient().put(policy, key3, bins3);
    aerospikeClient.getAerospikeClient().append(policy, key3, bins4);
    policy.expiration = 0;
    aerospikeClient.getAerospikeClient().put(policy, key4, bins4);
  }

  @Test
  public void getHeader(VertxTestContext testContext) {
    aerospikeClient.rxGetHeader(null, key3)
        .doOnSuccess(record -> {
          MatcherAssert.assertThat(record.generation, Matchers.equalTo(2));
          MatcherAssert.assertThat(record.expiration, Matchers.equalTo(0));
          MatcherAssert.assertThat(record.bins, Matchers.equalTo(null));
        })
        .doOnSuccess(record -> log.info("getHeader test passed!"))
        .subscribe(record -> testContext.completeNow(), testContext::failNow);
  }

  @Test
  public void getHeaderFromMultipleKeys(VertxTestContext testContext) {
    aerospikeClient.rxGetHeader(null, new Key[] {key3, key4})
        .doOnSuccess(records -> {
          MatcherAssert.assertThat(records.get(0).generation, Matchers.equalTo(2));
          MatcherAssert.assertThat(records.get(0).expiration, Matchers.equalTo(0));
          MatcherAssert.assertThat(records.get(0).bins, Matchers.equalTo(null));

          MatcherAssert.assertThat(records.get(1).generation, Matchers.equalTo(1));
          MatcherAssert.assertThat(records.get(1).expiration, Matchers.greaterThan(10000));
          MatcherAssert.assertThat(records.get(1).bins, Matchers.equalTo(null));
        })
        .doOnSuccess(record -> log.info("getHeaderFromMultipleKeys test passed!"))
        .subscribe(record -> testContext.completeNow(), testContext::failNow);
  }
}
