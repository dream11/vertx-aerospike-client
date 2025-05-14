package io.d11.aerospike;

import com.aerospike.client.Bin;
import com.aerospike.client.Key;
import com.aerospike.client.Operation;
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
public class AerospikeOperateTest {

  private static AerospikeClient aerospikeClient;
  private static final String testSet = "operateTestSet";
  private static final Key operateKey = new Key(Constants.TEST_NAMESPACE, testSet, "operate");
  private static final Bin[] bins = {new Bin("bin1", "Bangalore"), new Bin("bin2", 1)};
  private static final Bin[] appendBins = {new Bin("bin1", "Hyderabad"), new Bin("bin2", 2)};

  @BeforeAll
  public static void setup(Vertx vertx) {
    AerospikeConnectOptions connectOptions = new AerospikeConnectOptions()
        .setHost(System.getProperty(Constants.AEROSPIKE_HOST))
        .setPort(Integer.parseInt(System.getProperty(Constants.AEROSPIKE_PORT)));
    aerospikeClient = AerospikeClient.create(vertx, connectOptions);

    aerospikeClient.getAerospikeClient().put(null, operateKey, bins);
  }

  @AfterAll
  public static void cleanUp() {
    // remove test keys
    aerospikeClient.getAerospikeClient().truncate(null, Constants.TEST_NAMESPACE, testSet, null);
    aerospikeClient.close();
  }

  @Test
  public void operate(VertxTestContext testContext) {
    Operation[] operations = {
        Operation.append(appendBins[0]),
        Operation.add(appendBins[1]),
        Operation.get()
    };
    aerospikeClient.rxOperate(null, operateKey, operations)
        .doOnSuccess(record -> {
          MatcherAssert.assertThat(record.getString("bin1"), Matchers.equalTo("BangaloreHyderabad"));
          MatcherAssert.assertThat(record.getInt("bin2"), Matchers.equalTo(3));
        })
        .doOnSuccess(record -> log.info("operate test passed!"))
        .subscribe(record -> testContext.completeNow(), testContext::failNow);
  }
}
