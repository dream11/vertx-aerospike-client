package com.dream11.aerospike.config;

import com.typesafe.config.Optional;
import io.vertx.codegen.annotations.Fluent;
import lombok.Data;

@Data
public class AerospikeConnectOptions {
  static final String DEFAULT_HOST = "localhost";
  static final int DEFAULT_PORT = 3000;
  static final int DEFAULT_EVENT_LOOP_SIZE = 2 * Runtime.getRuntime().availableProcessors();
  static final int DEFAULT_MAX_COMMANDS_IN_PROCESS = 100;
  static final int DEFAULT_WRITE_TIMEOUT = 2000;
  static final int DEFAULT_READ_TIMEOUT = 1500;
  static final int DEFAULT_TEND_INTERVAL = 1000;
  static final int DEFAULT_MAX_CONNS_PER_NODE = DEFAULT_MAX_COMMANDS_IN_PROCESS * DEFAULT_EVENT_LOOP_SIZE;

  private String hosts = DEFAULT_HOST;
  @Optional
  private int port = DEFAULT_PORT;
  @Optional
  private int tendInterval = DEFAULT_TEND_INTERVAL;
  @Optional
  private int readTimeout = DEFAULT_READ_TIMEOUT;
  @Optional
  private int maxConnsPerNode = DEFAULT_MAX_CONNS_PER_NODE;
  @Optional
  private int eventLoopSize = DEFAULT_EVENT_LOOP_SIZE;
  @Optional
  private int maxCommandsInProcess = DEFAULT_MAX_COMMANDS_IN_PROCESS;
  @Optional
  private int writeTimeout = DEFAULT_WRITE_TIMEOUT;

  @Fluent
  public AerospikeConnectOptions setHosts(String hosts) {
    this.hosts = hosts;
    return this;
  }

  @Fluent
  public AerospikeConnectOptions setPort(int port) {
    this.port = port;
    return this;
  }

  @Fluent
  public AerospikeConnectOptions setTendInterval(int tendInterval) {
    this.tendInterval = tendInterval;
    return this;
  }

  @Fluent
  public AerospikeConnectOptions setReadTimeout(int readTimeout) {
    this.readTimeout = readTimeout;
    return this;
  }

  @Fluent
  public AerospikeConnectOptions setMaxConnsPerNode(int maxConnsPerNode) {
    this.maxConnsPerNode = maxConnsPerNode;
    return this;
  }

  @Fluent
  public AerospikeConnectOptions setEventLoopSize(int eventLoopSize) {
    this.eventLoopSize = eventLoopSize;
    return this;
  }

  @Fluent
  public AerospikeConnectOptions setMaxCommandsInProcess(int maxCommandsInProcess) {
    this.maxCommandsInProcess = maxCommandsInProcess;
    return this;
  }

  @Fluent
  public AerospikeConnectOptions setWriteTimeout(int writeTimeout) {
    this.writeTimeout = writeTimeout;
    return this;
  }
}
