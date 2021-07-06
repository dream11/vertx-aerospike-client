package com.dream11.aerospike.client;

import com.aerospike.client.AerospikeException;
import com.aerospike.client.BatchRead;
import com.aerospike.client.Bin;
import com.aerospike.client.Host;
import com.aerospike.client.Key;
import com.aerospike.client.Operation;
import com.aerospike.client.Record;
import com.aerospike.client.ResultCode;
import com.aerospike.client.Value;
import com.aerospike.client.async.EventLoops;
import com.aerospike.client.cluster.ClusterStats;
import com.aerospike.client.policy.BatchPolicy;
import com.aerospike.client.policy.Policy;
import com.aerospike.client.policy.QueryPolicy;
import com.aerospike.client.policy.ScanPolicy;
import com.aerospike.client.policy.WritePolicy;
import com.aerospike.client.query.KeyRecord;
import com.aerospike.client.query.PartitionFilter;
import com.aerospike.client.query.Statement;
import com.dream11.aerospike.config.AerospikeConnectOptions;
import com.dream11.aerospike.listeners.BatchListListenerImpl;
import com.dream11.aerospike.listeners.DeleteListenerImpl;
import com.dream11.aerospike.listeners.ExecuteListenerImpl;
import com.dream11.aerospike.listeners.ExistsArrayListenerImpl;
import com.dream11.aerospike.listeners.ExistsListenerImpl;
import com.dream11.aerospike.listeners.QueryResultListenerImpl;
import com.dream11.aerospike.listeners.RecordArrayListenerImpl;
import com.dream11.aerospike.listeners.RecordListenerImpl;
import com.dream11.aerospike.listeners.WriteListenerImpl;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.impl.VertxInternal;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AerospikeClientImpl implements AerospikeClient {
  private final VertxInternal vertx;
  private final EventLoops eventLoops;
  private final AerospikeConnectOptions connectOptions;
  private com.aerospike.client.AerospikeClient aerospikeClient;

  public AerospikeClientImpl(Vertx vertx, AerospikeConnectOptions connectOptions) {
    this.vertx = (VertxInternal) vertx;
    this.connectOptions = connectOptions;
    this.aerospikeClient = connectClientWithRetry();
    eventLoops = connectOptions.getClientPolicy().eventLoops;
  }

  public com.aerospike.client.AerospikeClient getAerospikeClient() {
    return this.aerospikeClient;
  }

  private <T> void schedule(Handler<Promise<T>> handler, Handler<AsyncResult<T>> resultHandler) {
    vertx.getOrCreateContext().executeBlocking(promise -> {
      try {
        handler.handle((Promise<T>) promise);
      } catch (AerospikeException e) {
        promise.fail(e);
      }
    }, result -> resultHandler.handle((AsyncResult<T>) result));
  }

  private com.aerospike.client.AerospikeClient connectClientWithRetry() {
    return connectClientWithRetry(0);
  }

  private com.aerospike.client.AerospikeClient connectClientWithRetry(int retryCount) {
    if (this.connectOptions.getMaxConnectRetries() != -1 && retryCount > this.connectOptions.getMaxConnectRetries()) {
      log.error("Exhausted max connection retries after {} attempts", retryCount);
      throw new AerospikeException(ResultCode.MAX_RETRIES_EXCEEDED, "Cannot connect to Aerospike");
    } else {
      try {
        Thread.sleep(2);
        return new com.aerospike.client.AerospikeClient(connectOptions.getClientPolicy(), new Host(connectOptions.getHost(),
            connectOptions.getPort()));
      } catch (Exception e) {
        log.error("Error while connecting to aerospike", e);
        log.info("retrying to connect to aerospike");
        return connectClientWithRetry(retryCount + 1);
      }
    }
  }

  /**
   * Determine if we are ready to talk to the database server cluster.
   *
   * @param handler the handler that will get the connection result
   */
  @Override
  public void isConnected(Handler<AsyncResult<Boolean>> handler) {
    this.schedule(promise -> promise.complete(this.aerospikeClient.isConnected()), handler);
  }

  /**
   * Return operating cluster statistics.
   */
  @Override
  public void getClusterStats(Handler<AsyncResult<ClusterStats>> handler) {
    this.schedule(promise -> promise.complete(this.aerospikeClient.getClusterStats()), handler);
  }

  /**
   * Close all client connections to database server nodes.
   * <p>
   * The client will send a cluster close signal to the event loops.
   * The client instance does not initiate shutdown
   * until the pending async commands complete.
   * <p>
   */
  public void close() {
    if (aerospikeClient != null) {
      aerospikeClient.close();
      aerospikeClient = null;
    }
  }


  /**
   * Asynchronously write record bin(s).
   * This method registers the command with an event loop.
   * The event loop thread will process the command and send the results back in handler.
   * <p>
   * The policy specifies the transaction timeout, record expiration and how the transaction is
   * handled when the record already exists.
   *
   * @param writePolicy write policy details
   * @param key         unique record identifier
   * @param bins        array of bin name/value pairs
   * @param handler     the handler that will get the result
   */
  public void put(WritePolicy writePolicy, Key key, Bin[] bins, Handler<AsyncResult<Key>> handler)
      throws AerospikeException {
    this.aerospikeClient.put(
        this.eventLoops.next(),
        new WriteListenerImpl(this.vertx.getOrCreateContext(), handler),
        writePolicy,
        key,
        bins);
  }

  /**
   * Asynchronously append bin string values to existing record bin values.
   * This method registers the command with an event loop.
   * The event loop thread will process the command and send the results back in handler.
   * <p>
   * The policy specifies the transaction timeout, record expiration and how the transaction is
   * handled when the record already exists.
   * This call only works for string values.
   *
   * @param writePolicy write policy details
   * @param key         unique record identifier
   * @param bins        array of bin name/value pairs
   * @param handler     the handler that will get the result
   */
  public void append(WritePolicy writePolicy, Key key, Bin[] bins, Handler<AsyncResult<Key>> handler)
      throws AerospikeException {
    this.aerospikeClient.append(
        this.eventLoops.next(),
        new WriteListenerImpl(this.vertx.getOrCreateContext(), handler),
        writePolicy,
        key,
        bins);
  }

  /**
   * Asynchronously prepend bin string values to existing record bin values.
   * This method registers the command with an event loop.
   * The event loop thread will process the command and send the results back in handler.
   * <p>
   * The policy specifies the transaction timeout, record expiration and how the transaction is
   * handled when the record already exists.
   * This call only works for string values.
   *
   * @param writePolicy write policy details
   * @param key         unique record identifier
   * @param bins        array of bin name/value pairs
   * @param handler     the handler that will get the result
   */
  public void prepend(
      WritePolicy writePolicy, Key key, Bin[] bins, Handler<AsyncResult<Key>> handler)
      throws AerospikeException {
    this.aerospikeClient.prepend(
        this.eventLoops.next(),
        new WriteListenerImpl(this.vertx.getOrCreateContext(), handler),
        writePolicy,
        key,
        bins);
  }

  public void add(WritePolicy writePolicy, Key key, Bin[] bins, Handler<AsyncResult<Key>> handler)
      throws AerospikeException {
    this.aerospikeClient.add(
        this.eventLoops.next(),
        new WriteListenerImpl(this.vertx.getOrCreateContext(), handler),
        writePolicy,
        key,
        bins);
  }

  public void delete(WritePolicy writePolicy, Key key, Handler<AsyncResult<Boolean>> handler)
      throws AerospikeException {
    this.aerospikeClient.delete(
        this.eventLoops.next(),
        new DeleteListenerImpl(this.vertx.getOrCreateContext(), handler),
        writePolicy,
        key);
  }

  public void touch(WritePolicy writePolicy, Key key, Handler<AsyncResult<Key>> handler)
      throws AerospikeException {
    this.aerospikeClient.touch(
        this.eventLoops.next(),
        new WriteListenerImpl(this.vertx.getOrCreateContext(), handler),
        writePolicy,
        key);
  }

  public void exists(Policy policy, Key key, Handler<AsyncResult<Boolean>> handler)
      throws AerospikeException {
    this.aerospikeClient.exists(
        this.eventLoops.next(),
        new ExistsListenerImpl(this.vertx.getOrCreateContext(), handler),
        policy,
        key);
  }

  // TODO: see the performance difference between ArrayListener and SequenceListener for all such batch commands
  public void exists(
      BatchPolicy batchPolicy, Key[] keys, Handler<AsyncResult<List<Boolean>>> handler)
      throws AerospikeException {
    this.aerospikeClient.exists(
        this.eventLoops.next(),
        new ExistsArrayListenerImpl(this.vertx.getOrCreateContext(), handler),
        batchPolicy,
        keys);
  }

  public void get(Policy policy, Key key, Handler<AsyncResult<Record>> handler)
      throws AerospikeException {
    this.aerospikeClient.get(
        this.eventLoops.next(),
        new RecordListenerImpl(this.vertx.getOrCreateContext(), handler),
        policy,
        key);
  }

  public void get(Policy policy, Key key, String[] binNames, Handler<AsyncResult<Record>> handler)
      throws AerospikeException {
    this.aerospikeClient.get(
        this.eventLoops.next(),
        new RecordListenerImpl(this.vertx.getOrCreateContext(), handler),
        policy,
        key,
        binNames);
  }

  public void getHeader(Policy policy, Key key, Handler<AsyncResult<Record>> handler)
      throws AerospikeException {
    this.aerospikeClient.getHeader(
        this.eventLoops.next(),
        new RecordListenerImpl(this.vertx.getOrCreateContext(), handler),
        policy,
        key);
  }

  public void get(
      BatchPolicy batchPolicy, List<BatchRead> list, Handler<AsyncResult<List<BatchRead>>> handler)
      throws AerospikeException {
    this.aerospikeClient.get(
        this.eventLoops.next(),
        new BatchListListenerImpl(this.vertx.getOrCreateContext(), handler),
        batchPolicy,
        list);
  }

  public void get(BatchPolicy batchPolicy, Key[] keys, Handler<AsyncResult<List<Record>>> handler)
      throws AerospikeException {
    this.aerospikeClient.get(
        this.eventLoops.next(),
        new RecordArrayListenerImpl(this.vertx.getOrCreateContext(), handler),
        batchPolicy,
        keys);
  }

  public void get(
      BatchPolicy batchPolicy,
      Key[] keys,
      String[] binNames,
      Handler<AsyncResult<List<Record>>> handler)
      throws AerospikeException {
    this.aerospikeClient.get(
        this.eventLoops.next(),
        new RecordArrayListenerImpl(this.vertx.getOrCreateContext(), handler),
        batchPolicy,
        keys,
        binNames);
  }

  public void getHeader(BatchPolicy batchPolicy, Key[] keys, Handler<AsyncResult<List<Record>>> handler)
      throws AerospikeException {
    this.aerospikeClient.getHeader(
        this.eventLoops.next(),
        new RecordArrayListenerImpl(this.vertx.getOrCreateContext(), handler),
        batchPolicy,
        keys);
  }

  public void operate(
      WritePolicy writePolicy,
      Key key,
      Operation[] operations,
      Handler<AsyncResult<Record>> handler)
      throws AerospikeException {
    this.aerospikeClient.operate(
        this.eventLoops.next(),
        new RecordListenerImpl(this.vertx.getOrCreateContext(), handler),
        writePolicy,
        key,
        operations);
  }

  public void scanAll(ScanPolicy policy, String namespace, String setName,
                      String[] binNames, Handler<AsyncResult<List<KeyRecord>>> handler)
      throws AerospikeException {
    this.aerospikeClient.scanAll(
        this.eventLoops.next(),
        new QueryResultListenerImpl(this.vertx.getOrCreateContext(), handler),
        policy,
        namespace,
        setName,
        binNames);
  }

  public void scanPartitions(ScanPolicy policy, PartitionFilter partitionFilter, String namespace,
                             String setName, String[] binNames, Handler<AsyncResult<List<KeyRecord>>> handler)
      throws AerospikeException {
    this.aerospikeClient.scanPartitions(
        this.eventLoops.next(),
        new QueryResultListenerImpl(this.vertx.getOrCreateContext(), handler),
        policy,
        partitionFilter,
        namespace,
        setName,
        binNames);
  }

  public void execute(
      WritePolicy writePolicy,
      Key key,
      String packageName,
      String functionName,
      Value[] functionArgs,
      Handler<AsyncResult<Object>> handler)
      throws AerospikeException {
    this.aerospikeClient.execute(
        this.eventLoops.next(),
        new ExecuteListenerImpl(this.vertx.getOrCreateContext(), handler),
        writePolicy,
        key,
        packageName,
        functionName,
        functionArgs);
  }

  // TODO: check if this works properly
  public void query(QueryPolicy queryPolicy, Statement statement, Handler<AsyncResult<List<KeyRecord>>> handler)
      throws AerospikeException {
    this.aerospikeClient.query(
        this.eventLoops.next(),
        new QueryResultListenerImpl(this.vertx.getOrCreateContext(), handler),
        queryPolicy,
        statement);
  }

}
