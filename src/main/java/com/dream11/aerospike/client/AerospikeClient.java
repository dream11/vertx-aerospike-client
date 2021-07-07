package com.dream11.aerospike.client;

import com.aerospike.client.AerospikeException;
import com.aerospike.client.BatchRead;
import com.aerospike.client.Bin;
import com.aerospike.client.Key;
import com.aerospike.client.Operation;
import com.aerospike.client.Record;
import com.aerospike.client.Value;
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
import com.dream11.aerospike.util.SharedDataUtils;
import io.vertx.codegen.annotations.GenIgnore;
import io.vertx.codegen.annotations.VertxGen;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import java.util.List;

@VertxGen
public interface AerospikeClient extends AutoCloseable {

  @GenIgnore(GenIgnore.PERMITTED_TYPE)
  static AerospikeClient create(Vertx vertx, AerospikeConnectOptions connectOptions) {
    String sharedInstanceName = "__AerospikeClient.__for.__" + connectOptions.getHost() + ":" + connectOptions.getPort();
    return SharedDataUtils.getOrCreate(new io.vertx.reactivex.core.Vertx(vertx), sharedInstanceName,
        () -> new AerospikeClientImpl(vertx, connectOptions.updateClientPolicy()));
  }

  @GenIgnore(GenIgnore.PERMITTED_TYPE)
  static AerospikeClient create(Vertx vertx) {
    return create(vertx, new AerospikeConnectOptions());
  }

  @GenIgnore(GenIgnore.PERMITTED_TYPE)
  static AerospikeClient createNonShared(Vertx vertx, AerospikeConnectOptions connectOptions) {
    return new AerospikeClientImpl(vertx, connectOptions.updateClientPolicy());
  }

  @GenIgnore(GenIgnore.PERMITTED_TYPE)
  static AerospikeClient createNonShared(Vertx vertx) {
    return createNonShared(vertx, new AerospikeConnectOptions());
  }

  void isConnected(Handler<AsyncResult<Boolean>> handler);

  @GenIgnore(GenIgnore.PERMITTED_TYPE)
  void getClusterStats(Handler<AsyncResult<ClusterStats>> handler);

  void close();

  @GenIgnore(GenIgnore.PERMITTED_TYPE)
  com.aerospike.client.AerospikeClient getAerospikeClient();

  @GenIgnore(GenIgnore.PERMITTED_TYPE)
  void put(WritePolicy writePolicy, Key key, Bin[] bins, Handler<AsyncResult<Key>> handler)
      throws AerospikeException;

  @GenIgnore(GenIgnore.PERMITTED_TYPE)
  void append(WritePolicy writePolicy, Key key, Bin[] bins, Handler<AsyncResult<Key>> handler)
      throws AerospikeException;

  @GenIgnore(GenIgnore.PERMITTED_TYPE)
  void prepend(WritePolicy writePolicy, Key key, Bin[] bins, Handler<AsyncResult<Key>> handler)
      throws AerospikeException;

  @GenIgnore(GenIgnore.PERMITTED_TYPE)
  void add(WritePolicy writePolicy, Key key, Bin[] bins, Handler<AsyncResult<Key>> handler)
      throws AerospikeException;

  @GenIgnore(GenIgnore.PERMITTED_TYPE)
  void delete(WritePolicy writePolicy, Key key, Handler<AsyncResult<Boolean>> handler)
      throws AerospikeException;

  @GenIgnore(GenIgnore.PERMITTED_TYPE)
  void touch(WritePolicy writePolicy, Key key, Handler<AsyncResult<Key>> handler)
      throws AerospikeException;

  @GenIgnore(GenIgnore.PERMITTED_TYPE)
  void exists(Policy policy, Key key, Handler<AsyncResult<Boolean>> handler)
      throws AerospikeException;

  @GenIgnore(GenIgnore.PERMITTED_TYPE)
  void exists(BatchPolicy batchPolicy, Key[] keys, Handler<AsyncResult<List<Boolean>>> handler)
      throws AerospikeException;

  @GenIgnore(GenIgnore.PERMITTED_TYPE)
  void get(Policy policy, Key key, Handler<AsyncResult<Record>> handler) throws AerospikeException;

  @GenIgnore(GenIgnore.PERMITTED_TYPE)
  void get(Policy policy, Key key, String[] binNames, Handler<AsyncResult<Record>> handler)
      throws AerospikeException;

  @GenIgnore(GenIgnore.PERMITTED_TYPE)
  void getHeader(Policy policy, Key key, Handler<AsyncResult<Record>> handler)
      throws AerospikeException;

  @GenIgnore(GenIgnore.PERMITTED_TYPE)
  void get(
      BatchPolicy batchPolicy, List<BatchRead> list, Handler<AsyncResult<List<BatchRead>>> handler)
      throws AerospikeException;

  @GenIgnore(GenIgnore.PERMITTED_TYPE)
  void get(BatchPolicy batchPolicy, Key[] keys, Handler<AsyncResult<List<Record>>> handler)
      throws AerospikeException;

  @GenIgnore(GenIgnore.PERMITTED_TYPE)
  void get(
      BatchPolicy batchPolicy,
      Key[] keys,
      String[] binNames,
      Handler<AsyncResult<List<Record>>> handler)
      throws AerospikeException;

  @GenIgnore(GenIgnore.PERMITTED_TYPE)
  void getHeader(BatchPolicy batchPolicy, Key[] keys, Handler<AsyncResult<List<Record>>> handler)
      throws AerospikeException;

  @GenIgnore(GenIgnore.PERMITTED_TYPE)
  void operate(
      WritePolicy writePolicy,
      Key key,
      Operation[] operations,
      Handler<AsyncResult<Record>> handler)
      throws AerospikeException;

  @GenIgnore(GenIgnore.PERMITTED_TYPE)
  public void scanAll(ScanPolicy policy, String namespace, String setName,
                      String[] binNames, Handler<AsyncResult<List<KeyRecord>>> handler)
      throws AerospikeException;

  @GenIgnore(GenIgnore.PERMITTED_TYPE)
  public void scanPartitions(ScanPolicy policy, PartitionFilter partitionFilter, String namespace,
                             String setName, String[] binNames, Handler<AsyncResult<List<KeyRecord>>> handler)
      throws AerospikeException;

  @GenIgnore(GenIgnore.PERMITTED_TYPE)
  void execute(
      WritePolicy writePolicy,
      Key key,
      String packageName,
      String functionName,
      Value[] functionArgs,
      Handler<AsyncResult<Object>> handler)
      throws AerospikeException;

  @GenIgnore(GenIgnore.PERMITTED_TYPE)
  void query(
      QueryPolicy queryPolicy, Statement statement, Handler<AsyncResult<List<KeyRecord>>> handler)
      throws AerospikeException;
}
