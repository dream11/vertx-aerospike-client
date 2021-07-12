package com.dream11.aerospike;

import com.aerospike.client.Bin;
import com.aerospike.client.Key;
import com.aerospike.client.Operation;
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
public class AerospikeOperateTest {

  private static AerospikeClient aerospikeClient;
  private static final Key key3 = new Key(Constants.TEST_NAMESPACE, Constants.TEST_SET, "pkey3");
  private static Bin[] bins = {new Bin("bin1", "Bangalore"), new Bin("bin2", 1)};
  private static Bin[] appendBins = {new Bin("bin1", "Hyderabad"), new Bin("bin2", 2)};

  @BeforeAll
  public static void setup(Vertx vertx) {
    AerospikeConnectOptions connectOptions = new AerospikeConnectOptions()
        .setHost(System.getProperty(Constants.AEROSPIKE_HOST))
        .setPort(Integer.parseInt(System.getProperty(Constants.AEROSPIKE_PORT)));
    aerospikeClient = AerospikeClient.create(vertx, connectOptions);

    aerospikeClient.getAerospikeClient().put(null, key3, bins);
  }

  @Test
  public void operate(VertxTestContext testContext) {
    Operation[] operations = {
        Operation.append(appendBins[0]),
        Operation.add(appendBins[1]),
        Operation.get()
    };
    aerospikeClient.rxOperate(null, key3, operations)
        .doOnSuccess(record -> {
          MatcherAssert.assertThat(record.getString("bin1"), Matchers.equalTo("BangaloreHyderabad"));
          MatcherAssert.assertThat(record.getInt("bin2"), Matchers.equalTo(3));
        })
        .doOnSuccess(record -> log.info("operate test passed!"))
        .subscribe(record -> testContext.completeNow(), testContext::failNow);
  }
}
