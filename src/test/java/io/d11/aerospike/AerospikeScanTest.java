package io.d11.aerospike;

import com.aerospike.client.query.PartitionFilter;
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
public class AerospikeScanTest {
  private static AerospikeClient aerospikeClient;

  @BeforeAll
  public static void setup(Vertx vertx) {
    AerospikeConnectOptions connectOptions = new AerospikeConnectOptions()
        .setHost(System.getProperty(Constants.AEROSPIKE_HOST))
        .setPort(Integer.parseInt(System.getProperty(Constants.AEROSPIKE_PORT)));
    aerospikeClient = AerospikeClient.create(vertx, connectOptions);
  }

  @AfterAll
  public static void cleanUp() {
    aerospikeClient.close();
  }

  @Test
  public void scanAll(VertxTestContext testContext) {
    aerospikeClient.rxScanAll(null, Constants.TEST_NAMESPACE, Constants.TEST_SET, new String[]{"bin1"})
        .doOnSuccess(keyRecords -> {
          MatcherAssert.assertThat(keyRecords.size(), Matchers.equalTo(2));
          keyRecords.forEach(keyRecord ->
              MatcherAssert.assertThat(keyRecord.record.getString("bin1"), Matchers.in(new String[]{"Mumbai", "Delhi"})));
        })
        .doOnSuccess(record -> log.info("scanAll test passed!"))
        .subscribe(record -> testContext.completeNow(), testContext::failNow);
  }

  @Test
  public void scanPartitions(VertxTestContext testContext) {
    aerospikeClient.rxScanPartitions(null, PartitionFilter.range(0, 4095),
        Constants.TEST_NAMESPACE, Constants.TEST_SET, new String[]{"bin1"})
        .doOnSuccess(keyRecords -> {
          MatcherAssert.assertThat(keyRecords.size(), Matchers.equalTo(2));
          keyRecords.forEach(keyRecord ->
              MatcherAssert.assertThat(keyRecord.record.getString("bin1"), Matchers.in(new String[]{"Mumbai", "Delhi"})));
        })
        .doOnSuccess(record -> log.info("scanPartitions test passed!"))
        .subscribe(record -> testContext.completeNow(), testContext::failNow);
  }

}
