package io.d11.aerospike.client;

import com.aerospike.client.async.EventPolicy;
import com.aerospike.client.async.NettyEventLoops;
import com.aerospike.client.policy.ClientPolicy;
import com.aerospike.client.policy.Replica;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.vertx.codegen.annotations.Fluent;
import lombok.Data;

@Data
public class AerospikeConnectOptions {
  static final String OS = System.getProperty("os.name");
  static final String DEFAULT_HOST = "localhost";
  static final int DEFAULT_PORT = 3000;
  static final int DEFAULT_EVENT_LOOP_SIZE = 2 * Runtime.getRuntime().availableProcessors();
  static final int DEFAULT_MAX_COMMANDS_IN_PROCESS = 100;
  static final int DEFAULT_MAX_CONNS_PER_NODE = DEFAULT_MAX_COMMANDS_IN_PROCESS * DEFAULT_EVENT_LOOP_SIZE;
  static final int DEFAULT_MAX_CONNECT_RETRIES = 2;

  private String host;
  private int port;
  private int maxConnsPerNode;
  private int eventLoopSize;
  private int maxCommandsInProcess;
  private int maxCommandsInQueue;
  private ClientPolicy clientPolicy;
  private int maxConnectRetries;

  public AerospikeConnectOptions() {
    ClientPolicy clientPolicy = new ClientPolicy();
    clientPolicy.readPolicyDefault.replica = Replica.MASTER_PROLES;
    this.clientPolicy = clientPolicy;
    this.host = DEFAULT_HOST;
    this.port = DEFAULT_PORT;
    this.maxConnsPerNode = DEFAULT_MAX_CONNS_PER_NODE;
    this.eventLoopSize = DEFAULT_EVENT_LOOP_SIZE;
    this.maxCommandsInProcess = DEFAULT_MAX_COMMANDS_IN_PROCESS;
    this.maxConnectRetries = DEFAULT_MAX_CONNECT_RETRIES;
  }

  @Fluent
  public AerospikeConnectOptions setMaxConnectRetries(int maxConnectRetries) {
    this.maxConnectRetries = maxConnectRetries;
    return this;
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
  public AerospikeConnectOptions setMaxCommandsInQueue(int maxCommandsInQueue) {
    this.maxCommandsInQueue = maxCommandsInQueue;
    return this;
  }

  @Fluent
  public AerospikeConnectOptions updateClientPolicy() {
    EventPolicy eventPolicy = new EventPolicy();
    eventPolicy.maxCommandsInProcess = this.getMaxCommandsInProcess();
    eventPolicy.maxCommandsInQueue = this.getMaxCommandsInQueue();
    EventLoopGroup group = getEventLoopGroup(this.getEventLoopSize());
    this.clientPolicy.eventLoops = new NettyEventLoops(eventPolicy, group);
    this.clientPolicy.maxConnsPerNode = this.getMaxConnsPerNode();
    return this;
  }

  private EventLoopGroup getEventLoopGroup(int size) {
    return OS.contains("linux") || OS.contains("unix") ?
        new EpollEventLoopGroup(size) : new NioEventLoopGroup(size);
  }
}
