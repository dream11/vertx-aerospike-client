package com.dream11.aerospike.config;

import com.aerospike.client.policy.ClientPolicy;
import com.aerospike.client.policy.Replica;
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

  private String host;
  private int port;
  private int tendInterval;
  private int readTimeout;
  private int maxConnsPerNode;
  private int eventLoopSize;
  private int maxCommandsInProcess;
  private int writeTimeout;
  private ClientPolicy clientPolicy;

  public AerospikeConnectOptions() {
    ClientPolicy clientPolicy = new ClientPolicy();
    clientPolicy.readPolicyDefault.replica = Replica.MASTER_PROLES;
    this.clientPolicy = clientPolicy;
    this.host = DEFAULT_HOST;
    this.port = DEFAULT_PORT;
    this.tendInterval = DEFAULT_TEND_INTERVAL;
    this.readTimeout = DEFAULT_READ_TIMEOUT;
    this.maxConnsPerNode = DEFAULT_MAX_CONNS_PER_NODE;
    this.eventLoopSize = DEFAULT_EVENT_LOOP_SIZE;
    this.maxCommandsInProcess = DEFAULT_MAX_COMMANDS_IN_PROCESS;
    this.writeTimeout = DEFAULT_WRITE_TIMEOUT;

  }

  @Fluent
  public AerospikeConnectOptions setClientPolicy(ClientPolicy clientPolicy) {
    this.clientPolicy = clientPolicy;
    return this;
  }

  @Fluent
  public AerospikeConnectOptions setHost(String host) {
    this.host = host;
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
