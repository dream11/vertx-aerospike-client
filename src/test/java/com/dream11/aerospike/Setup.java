package com.dream11.aerospike;

import java.io.IOException;
import java.time.Duration;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.testcontainers.containers.BindMode;
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
  public void beforeAll(ExtensionContext extensionContext) throws IOException, InterruptedException {
    aerospikeContainer = new GenericContainer<>(AEROSPIKE_IMAGE);
    log.info("Starting aerospike client from image:{}", AEROSPIKE_IMAGE);
    aerospikeContainer
        .withEnv("NAMESPACE", "test")
        .withExposedPorts(3000)
        .withStartupTimeout(Duration.ofSeconds(1000))
        .withStartupCheckStrategy(new MinimumDurationRunningStartupCheckStrategy(Duration.ofSeconds(10)))
        .addFileSystemBind("src/test/resources/init.aql", "/aerospike-seed/init.aql", BindMode.READ_ONLY);
    aerospikeContainer.start();
    // add seed data in containers
    aerospikeContainer.execInContainer("aql", "-f", "aerospike-seed/init.aql");
    System.setProperty("aerospike.host", aerospikeContainer.getHost());
    System.setProperty("aerospike.port", String.valueOf(aerospikeContainer.getFirstMappedPort()));
  }

  @Override
  public void close() {

  }
}
