package io.d11.aerospike;

import com.aerospike.client.BatchRead;
import com.aerospike.client.Key;
import io.d11.aerospike.client.AerospikeConnectOptions;
import io.d11.reactivex.aerospike.client.AerospikeClient;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import io.vertx.reactivex.core.Vertx;
import java.util.Arrays;
import lombok.extern.slf4j.Slf4j;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith({VertxExtension.class, Setup.class})
@Slf4j
public class AerospikeGetTest {

  private static AerospikeClient aerospikeClient;
  private final Key key1 = new Key(Constants.TEST_NAMESPACE, Constants.TEST_SET, "pkey1");
  private final Key key2 = new Key(Constants.TEST_NAMESPACE, Constants.TEST_SET, "pkey2");

  @BeforeAll
  public static void setup(Vertx vertx) {
    AerospikeConnectOptions connectOptions = new AerospikeConnectOptions()
        .setHost(System.getProperty(Constants.AEROSPIKE_HOST))
        .setPort(Integer.parseInt(System.getProperty(Constants.AEROSPIKE_PORT)));
    aerospikeClient = AerospikeClient.create(vertx, connectOptions);
  }

  @AfterAll
  public static void cleanUp() {
    // remove test keys
    aerospikeClient.close();
  }

  @Test
  public void getAllBins(VertxTestContext testContext) {
    aerospikeClient.rxGet(null, key1)
        .doOnSuccess(record -> {
          MatcherAssert.assertThat(record.getString("bin1"), Matchers.equalTo("Mumbai"));
          MatcherAssert.assertThat(record.getInt("bin2"), Matchers.equalTo(123));
        })
        .doOnSuccess(record -> log.info("getAllBins test passed!"))
        .subscribe(record -> testContext.completeNow(), testContext::failNow);
  }

  @Test
  public void getSelectedBins(VertxTestContext testContext) {
    aerospikeClient.rxGet(null, key1, new String[] {"bin1"})
        .doOnSuccess(record -> {
          MatcherAssert.assertThat(record.bins.size(), Matchers.equalTo(1));
          MatcherAssert.assertThat(record.getString("bin1"), Matchers.equalTo("Mumbai"));
        })
        .doOnSuccess(record -> log.info("getSelectedBins test passed!"))
        .subscribe(record -> testContext.completeNow(), testContext::failNow);
  }

  @Test
  public void getAllBinsFromMultipleKeys(VertxTestContext testContext) {
    aerospikeClient.rxGet(null, new Key[] {key1, key2})
        .doOnSuccess(records -> {
          MatcherAssert.assertThat(records.get(0).getString("bin1"), Matchers.equalTo("Mumbai"));
          MatcherAssert.assertThat(records.get(0).getInt("bin2"), Matchers.equalTo(123));

          MatcherAssert.assertThat(records.get(1).getString("bin1"), Matchers.equalTo("Delhi"));
          MatcherAssert.assertThat(records.get(1).getInt("bin2"), Matchers.equalTo(3));
        })
        .doOnSuccess(record -> log.info("getAllBinsFromMultipleKeys test passed!"))
        .subscribe(record -> testContext.completeNow(), testContext::failNow);
  }

  @Test
  public void getSelectedBinsFromMultipleKeys(VertxTestContext testContext) {
    aerospikeClient.rxGet(null, new Key[] {key1, key2}, new String[] {"bin1"})
        .doOnSuccess(record -> {
          MatcherAssert.assertThat(record.get(0).bins.size(), Matchers.equalTo(1));
          MatcherAssert.assertThat(record.get(0).getString("bin1"), Matchers.equalTo("Mumbai"));

          MatcherAssert.assertThat(record.get(1).bins.size(), Matchers.equalTo(1));
          MatcherAssert.assertThat(record.get(1).getString("bin1"), Matchers.equalTo("Delhi"));
        })
        .doOnSuccess(record -> log.info("getSelectedBinsFromMultipleKeys test passed!"))
        .subscribe(record -> testContext.completeNow(), testContext::failNow);
  }

  @Test
  public void getDifferentBinsFromMultipleKeys(VertxTestContext testContext) {
    BatchRead batchRead1 = new BatchRead(key1, true);
    BatchRead batchRead2 = new BatchRead(key2, new String[] {"bin1"});
    aerospikeClient.rxGet(null, Arrays.asList(batchRead1, batchRead2))
        .doOnSuccess(record -> {
          MatcherAssert.assertThat(record.get(0).record.getString("bin1"), Matchers.equalTo("Mumbai"));
          MatcherAssert.assertThat(record.get(0).record.getInt("bin2"), Matchers.equalTo(123));

          MatcherAssert.assertThat(record.get(1).record.bins.size(), Matchers.equalTo(1));
          MatcherAssert.assertThat(record.get(1).record.getString("bin1"), Matchers.equalTo("Delhi"));
        })
        .doOnSuccess(record -> log.info("getDifferentBinsFromMultipleKeys test passed!"))
        .subscribe(record -> testContext.completeNow(), testContext::failNow);
  }

}
