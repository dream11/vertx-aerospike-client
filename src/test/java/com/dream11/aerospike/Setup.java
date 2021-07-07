package com.dream11.aerospike;

import java.time.Duration;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.startupcheck.MinimumDurationRunningStartupCheckStrategy;

@Slf4j
public class Setup implements BeforeAllCallback, AfterAllCallback, ExtensionContext.Store.CloseableResource {
  public static final String AEROSPIKE_IMAGE = System.getProperty("aerospike.image", "aerospike/aerospike-server");

  GenericContainer aerospikeContainer;

  @Override
  public void afterAll(ExtensionContext extensionContext) {
    aerospikeContainer.close();
  }

  @Override
  public void beforeAll(ExtensionContext extensionContext) {
    aerospikeContainer = new GenericContainer<>(AEROSPIKE_IMAGE);
    log.info("Starting aerospike client from image:{}", AEROSPIKE_IMAGE);
    aerospikeContainer
        .withExposedPorts(3000)
        .withStartupTimeout(Duration.ofSeconds(1000))
        .withStartupCheckStrategy(new MinimumDurationRunningStartupCheckStrategy(Duration.ofSeconds(10)));
    aerospikeContainer.start();
    System.setProperty("aerospike.host", aerospikeContainer.getHost());
    System.setProperty("aerospike.port", String.valueOf(aerospikeContainer.getFirstMappedPort()));
  }

  @Override
  public void close() {

  }
}
