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
    this.aerospikeClient = connectClientWithRetry(0);
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
        log.info("Retrying to connect to aerospike");
        return connectClientWithRetry(retryCount + 1);
      }
    }
  }

  @Override
  public AerospikeClient isConnected(Handler<AsyncResult<Boolean>> handler) {
    this.schedule(promise -> promise.complete(this.aerospikeClient.isConnected()), handler);
    return this;
  }

  @Override
  public AerospikeClient getClusterStats(Handler<AsyncResult<ClusterStats>> handler) {
    this.schedule(promise -> promise.complete(this.aerospikeClient.getClusterStats()), handler);
    return this;
  }

  public void close() {
    if (aerospikeClient != null) {
      aerospikeClient.close();
      aerospikeClient = null;
    }
  }

  public AerospikeClient put(WritePolicy writePolicy, Key key, Bin[] bins, Handler<AsyncResult<Key>> handler)
      throws AerospikeException {
    this.aerospikeClient.put(
        this.eventLoops.next(),
        new WriteListenerImpl(this.vertx.getOrCreateContext(), handler),
        writePolicy,
        key,
        bins);
    return this;
  }

  public AerospikeClient append(WritePolicy writePolicy, Key key, Bin[] bins, Handler<AsyncResult<Key>> handler)
      throws AerospikeException {
    this.aerospikeClient.append(
        this.eventLoops.next(),
        new WriteListenerImpl(this.vertx.getOrCreateContext(), handler),
        writePolicy,
        key,
        bins);
    return this;
  }

  public AerospikeClient prepend(
      WritePolicy writePolicy, Key key, Bin[] bins, Handler<AsyncResult<Key>> handler)
      throws AerospikeException {
    this.aerospikeClient.prepend(
        this.eventLoops.next(),
        new WriteListenerImpl(this.vertx.getOrCreateContext(), handler),
        writePolicy,
        key,
        bins);
    return this;
  }

  public AerospikeClient add(WritePolicy writePolicy, Key key, Bin[] bins, Handler<AsyncResult<Key>> handler)
      throws AerospikeException {
    this.aerospikeClient.add(
        this.eventLoops.next(),
        new WriteListenerImpl(this.vertx.getOrCreateContext(), handler),
        writePolicy,
        key,
        bins);
    return this;
  }

  public AerospikeClient delete(WritePolicy writePolicy, Key key, Handler<AsyncResult<Boolean>> handler)
      throws AerospikeException {
    this.aerospikeClient.delete(
        this.eventLoops.next(),
        new DeleteListenerImpl(this.vertx.getOrCreateContext(), handler),
        writePolicy,
        key);
    return this;
  }

  public AerospikeClient touch(WritePolicy writePolicy, Key key, Handler<AsyncResult<Key>> handler)
      throws AerospikeException {
    this.aerospikeClient.touch(
        this.eventLoops.next(),
        new WriteListenerImpl(this.vertx.getOrCreateContext(), handler),
        writePolicy,
        key);
    return this;
  }

  public AerospikeClient exists(Policy policy, Key key, Handler<AsyncResult<Boolean>> handler)
      throws AerospikeException {
    this.aerospikeClient.exists(
        this.eventLoops.next(),
        new ExistsListenerImpl(this.vertx.getOrCreateContext(), handler),
        policy,
        key);
    return this;
  }

  public AerospikeClient exists(
      BatchPolicy batchPolicy, Key[] keys, Handler<AsyncResult<List<Boolean>>> handler)
      throws AerospikeException {
    this.aerospikeClient.exists(
        this.eventLoops.next(),
        new ExistsArrayListenerImpl(this.vertx.getOrCreateContext(), handler),
        batchPolicy,
        keys);
    return this;
  }

  public AerospikeClient get(Policy policy, Key key, Handler<AsyncResult<Record>> handler)
      throws AerospikeException {
    this.aerospikeClient.get(
        this.eventLoops.next(),
        new RecordListenerImpl(this.vertx.getOrCreateContext(), handler),
        policy,
        key);
    return this;
  }

  public AerospikeClient get(Policy policy, Key key, String[] binNames, Handler<AsyncResult<Record>> handler)
      throws AerospikeException {
    this.aerospikeClient.get(
        this.eventLoops.next(),
        new RecordListenerImpl(this.vertx.getOrCreateContext(), handler),
        policy,
        key,
        binNames);
    return this;
  }

  public AerospikeClient getHeader(Policy policy, Key key, Handler<AsyncResult<Record>> handler)
      throws AerospikeException {
    this.aerospikeClient.getHeader(
        this.eventLoops.next(),
        new RecordListenerImpl(this.vertx.getOrCreateContext(), handler),
        policy,
        key);
    return this;
  }

  public AerospikeClient get(
      BatchPolicy batchPolicy, List<BatchRead> records, Handler<AsyncResult<List<BatchRead>>> handler)
      throws AerospikeException {
    this.aerospikeClient.get(
        this.eventLoops.next(),
        new BatchListListenerImpl(this.vertx.getOrCreateContext(), handler),
        batchPolicy,
        records);
    return this;
  }

  public AerospikeClient get(BatchPolicy batchPolicy, Key[] keys, Handler<AsyncResult<List<Record>>> handler)
      throws AerospikeException {
    this.aerospikeClient.get(
        this.eventLoops.next(),
        new RecordArrayListenerImpl(this.vertx.getOrCreateContext(), handler),
        batchPolicy,
        keys);
    return this;
  }

  public AerospikeClient get(
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
    return this;
  }

  public AerospikeClient getHeader(BatchPolicy batchPolicy, Key[] keys, Handler<AsyncResult<List<Record>>> handler)
      throws AerospikeException {
    this.aerospikeClient.getHeader(
        this.eventLoops.next(),
        new RecordArrayListenerImpl(this.vertx.getOrCreateContext(), handler),
        batchPolicy,
        keys);
    return this;
  }

  public AerospikeClient operate(
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
    return this;
  }

  public AerospikeClient scanAll(ScanPolicy policy, String namespace, String setName,
                      String[] binNames, Handler<AsyncResult<List<KeyRecord>>> handler)
      throws AerospikeException {
    this.aerospikeClient.scanAll(
        this.eventLoops.next(),
        new QueryResultListenerImpl(this.vertx.getOrCreateContext(), handler),
        policy,
        namespace,
        setName,
        binNames);
    return this;
  }

  public AerospikeClient scanPartitions(ScanPolicy policy, PartitionFilter partitionFilter, String namespace,
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
    return this;
  }

  public AerospikeClient execute(
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
    return this;
  }

  public AerospikeClient query(QueryPolicy queryPolicy, Statement statement, Handler<AsyncResult<List<KeyRecord>>> handler)
      throws AerospikeException {
    this.aerospikeClient.query(
        this.eventLoops.next(),
        new QueryResultListenerImpl(this.vertx.getOrCreateContext(), handler),
        queryPolicy,
        statement);
    return this;
  }

}
