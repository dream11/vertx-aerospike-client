package com.dream11.aerospike;

import com.aerospike.client.query.Statement;
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
public class AerospikeQueryTest {
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
  public void query(VertxTestContext testContext) {
    Statement statement = new Statement();
    statement.setNamespace(Constants.TEST_NAMESPACE);
    statement.setSetName(Constants.TEST_SET);
    statement.setBinNames("bin1", "bin2");
    aerospikeClient.rxQuery(null, statement)
        .doOnSuccess(keyRecords -> {
          MatcherAssert.assertThat(keyRecords.size(), Matchers.equalTo(2));
          keyRecords.forEach(keyRecord -> {
            MatcherAssert.assertThat(keyRecord.record.getString("bin1"), Matchers.in(new String[]{"Mumbai", "Delhi"}));
            if (keyRecord.record.getString("bin1").equals("Mumbai")) {
              MatcherAssert.assertThat(keyRecord.record.getInt("bin2"), Matchers.equalTo(123));
            } else {
              MatcherAssert.assertThat(keyRecord.record.getInt("bin2"), Matchers.equalTo(3));
            }
          });
        })
        .doOnSuccess(record -> log.info("query test passed!"))
        .subscribe(record -> testContext.completeNow(), testContext::failNow);
  }
}
