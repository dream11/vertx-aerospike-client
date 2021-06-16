package com.dream11.aerospike.client;

import com.aerospike.client.*;
import com.aerospike.client.async.EventLoops;
import com.aerospike.client.cluster.ClusterStats;
import com.aerospike.client.policy.*;
import com.aerospike.client.query.KeyRecord;
import com.aerospike.client.query.PartitionFilter;
import com.aerospike.client.query.Statement;
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
public class AerospikeClientImpl extends com.aerospike.client.AerospikeClient implements AerospikeClient {
  final VertxInternal vertx;
  private EventLoops eventLoops;

  public com.aerospike.client.AerospikeClient getAerospikeClient() {
    return this;
  }

  public AerospikeClientImpl(Vertx vertx, ClientPolicy policy, Host... hosts) {
    super(policy, hosts);
    this.vertx = (VertxInternal) vertx;
    eventLoops = policy.eventLoops;
  }

  private  <T> void schedule(Handler<Promise<T>> handler, Handler<AsyncResult<T>> resultHandler) {
    vertx.getOrCreateContext().executeBlocking(promise -> {
      try {
        handler.handle((Promise<T>)promise);
      } catch (AerospikeException e) {
        promise.fail(e);
      }
    }, result -> resultHandler.handle((AsyncResult<T>) result));
  }

  /**
   * Determine if we are ready to talk to the database server cluster.
   *
   * @param handler the handler that will get the connection result
   */
  @Override
  public void isConnected(Handler<AsyncResult<Boolean>> handler) {
    this.schedule(promise -> promise.complete(isConnected()), handler);
  }

  /**
   * Return operating cluster statistics.
   */
  @Override
  public void getClusterStats(Handler<AsyncResult<ClusterStats>> handler) {
    this.schedule(promise -> promise.complete(getClusterStats()), handler);
  }

  //TODO: what is this?
  public void connect(Handler<AsyncResult<Void>> handler) { }

  /**
   * Close all client connections to database server nodes.
   * <p>
   * The client will send a cluster close signal to the event loops.
   * The client instance does not initiate shutdown
   * until the pending async commands complete.
   * <p>
   */
  public void close() {
    super.close();
  }


  /**
   * Asynchronously write record bin(s).
   * This method registers the command with an event loop.
   * The event loop thread will process the command and send the results back in handler.
   * <p>
   * The policy specifies the transaction timeout, record expiration and how the transaction is
   * handled when the record already exists.
   *
   * @param writePolicy	 write policy details
   * @param key					unique record identifier
   * @param bins				array of bin name/value pairs
   * @param handler     the handler that will get the result
   */
  public void put(WritePolicy writePolicy, Key key, Bin[] bins, Handler<AsyncResult<Key>> handler)
      throws AerospikeException {
    this.put(
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
   * @param writePolicy	 write policy details
   * @param key					unique record identifier
   * @param bins					array of bin name/value pairs
   * @param handler     the handler that will get the result
   */
  public void append(WritePolicy writePolicy, Key key, Bin[] bins, Handler<AsyncResult<Key>> handler)
      throws AerospikeException {
    this.append(
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
   * @param writePolicy	 write policy details
   * @param key					unique record identifier
   * @param bins					array of bin name/value pairs
   * @param handler     the handler that will get the result
   */
  public void prepend(
      WritePolicy writePolicy, Key key, Bin[] bins, Handler<AsyncResult<Key>> handler)
      throws AerospikeException {
    this.prepend(
        this.eventLoops.next(),
        new WriteListenerImpl(this.vertx.getOrCreateContext(), handler),
        writePolicy,
        key,
        bins);
  }

  public void add(WritePolicy writePolicy, Key key, Bin[] bins, Handler<AsyncResult<Key>> handler)
      throws AerospikeException {
    this.add(
        this.eventLoops.next(),
        new WriteListenerImpl(this.vertx.getOrCreateContext(), handler),
        writePolicy,
        key,
        bins);
  }

  public void delete(WritePolicy writePolicy, Key key, Handler<AsyncResult<Boolean>> handler)
      throws AerospikeException {
    this.delete(
        this.eventLoops.next(),
        new DeleteListenerImpl(this.vertx.getOrCreateContext(), handler),
        writePolicy,
        key);
  }

  public void touch(WritePolicy writePolicy, Key key, Handler<AsyncResult<Key>> handler)
      throws AerospikeException {
    this.touch(
        this.eventLoops.next(),
        new WriteListenerImpl(this.vertx.getOrCreateContext(), handler),
        writePolicy,
        key);
  }

  public void exists(Policy policy, Key key, Handler<AsyncResult<Boolean>> handler)
      throws AerospikeException {
    this.exists(
        this.eventLoops.next(),
        new ExistsListenerImpl(this.vertx.getOrCreateContext(), handler),
        policy,
        key);
  }

  // TODO: see the performance difference between ArrayListener and SequenceListener for all such batch commands
  public void exists(
      BatchPolicy batchPolicy, Key[] keys, Handler<AsyncResult<List<Boolean>>> handler)
      throws AerospikeException {
    this.exists(
        this.eventLoops.next(),
        new ExistsArrayListenerImpl(this.vertx.getOrCreateContext(), handler),
        batchPolicy,
        keys);
  }

  public void get(Policy policy, Key key, Handler<AsyncResult<Record>> handler)
      throws AerospikeException {
    this.get(
        this.eventLoops.next(),
        new RecordListenerImpl(this.vertx.getOrCreateContext(), handler),
        policy,
        key);
  }

  public void get(Policy policy, Key key, String[] binNames, Handler<AsyncResult<Record>> handler)
      throws AerospikeException {
    this.get(
        this.eventLoops.next(),
        new RecordListenerImpl(this.vertx.getOrCreateContext(), handler),
        policy,
        key,
        binNames);
  }

  public void getHeader(Policy policy, Key key, Handler<AsyncResult<Record>> handler)
      throws AerospikeException {
    this.getHeader(
        this.eventLoops.next(),
        new RecordListenerImpl(this.vertx.getOrCreateContext(), handler),
        policy,
        key);
  }

  public void get(
      BatchPolicy batchPolicy, List<BatchRead> list, Handler<AsyncResult<List<BatchRead>>> handler)
      throws AerospikeException {
    this.get(
        this.eventLoops.next(),
        new BatchListListenerImpl(this.vertx.getOrCreateContext(), handler),
        batchPolicy,
        list);
  }

  public void get(BatchPolicy batchPolicy, Key[] keys, Handler<AsyncResult<List<Record>>> handler)
      throws AerospikeException {
    this.get(
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
    this.get(
        this.eventLoops.next(),
        new RecordArrayListenerImpl(this.vertx.getOrCreateContext(), handler),
        batchPolicy,
        keys,
        binNames);
  }

  public void getHeader(BatchPolicy batchPolicy, Key[] keys, Handler<AsyncResult<List<Record>>> handler)
      throws AerospikeException {
    this.getHeader(
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
    this.operate(
        this.eventLoops.next(),
        new RecordListenerImpl(this.vertx.getOrCreateContext(), handler),
        writePolicy,
        key,
        operations);
  }

  public void scanAll(ScanPolicy policy, String namespace, String setName,
                      String[] binNames, Handler<AsyncResult<List<KeyRecord>>> handler)
      throws AerospikeException {
    this.scanAll(
        this.eventLoops.next(),
        new QueryResultListenerImpl(this.vertx.getOrCreateContext(), handler),
        policy,
        namespace,
        setName,
        binNames);
  }

  public void scanPartitions(ScanPolicy policy, PartitionFilter partitionFilter, String namespace,
                             String setName, String[] binNames, Handler<AsyncResult<List<KeyRecord>>> handler)
      throws AerospikeException{
    this.scanPartitions(
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
    this.execute(
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
    this.query(
        this.eventLoops.next(),
        new QueryResultListenerImpl(this.vertx.getOrCreateContext(), handler),
        queryPolicy,
        statement);
  }

}
