package com.dream11.aerospike.client;

import com.aerospike.client.AerospikeException;
import com.aerospike.client.BatchRead;
import com.aerospike.client.Bin;
import com.aerospike.client.Key;
import com.aerospike.client.Operation;
import com.aerospike.client.Record;
import com.aerospike.client.Value;
import com.aerospike.client.async.EventLoop;
import com.aerospike.client.cluster.ClusterStats;
import com.aerospike.client.listener.BatchListListener;
import com.aerospike.client.listener.DeleteListener;
import com.aerospike.client.listener.ExecuteListener;
import com.aerospike.client.listener.ExistsArrayListener;
import com.aerospike.client.listener.ExistsListener;
import com.aerospike.client.listener.RecordArrayListener;
import com.aerospike.client.listener.RecordListener;
import com.aerospike.client.listener.RecordSequenceListener;
import com.aerospike.client.listener.WriteListener;
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


/**
 * The {@code AerospikeClient} interface provides an abstraction on top of {@link com.aerospike.client.AerospikeClient aerospike's client}
 * for integrating its asynchronous commands in <a href=https://io.vertx>Vert.x</a>-based Applications.
 */
@VertxGen
public interface AerospikeClient extends AutoCloseable {

  /**
   * Create a shared aerospike client using the given connect options.
   * The client will be shared across a vertx instance
   * @param vertx the vertx instance
   * @param connectOptions user provided connection options
   * @return the client
   */
  @GenIgnore(GenIgnore.PERMITTED_TYPE)
  static AerospikeClient create(Vertx vertx, AerospikeConnectOptions connectOptions) {
    String sharedInstanceName = "__AerospikeClient.__for.__" + connectOptions.getHost() + ":" + connectOptions.getPort();
    return SharedDataUtils.getOrCreate(new io.vertx.reactivex.core.Vertx(vertx), sharedInstanceName,
        () -> new AerospikeClientImpl(vertx, connectOptions.updateClientPolicy()));
  }

  /**
   * Like {@link AerospikeClient#create(Vertx, AerospikeConnectOptions)} with default options.
   * @param vertx the vertx instance
   * @return the client
   */
  @GenIgnore(GenIgnore.PERMITTED_TYPE)
  static AerospikeClient create(Vertx vertx) {
    return create(vertx, new AerospikeConnectOptions());
  }

  /**
   * Create a non shared aerospike client using the given connect options.
   * It is not recommended to create several non shared clients in an application.
   * @param vertx the vertx instance
   * @param connectOptions user provided connection options
   * @return the client
   */
  @GenIgnore(GenIgnore.PERMITTED_TYPE)
  static AerospikeClient createNonShared(Vertx vertx, AerospikeConnectOptions connectOptions) {
    return new AerospikeClientImpl(vertx, connectOptions.updateClientPolicy());
  }

  /**
   * Like {@link AerospikeClient#createNonShared(Vertx, AerospikeConnectOptions)} with default options.
   * @param vertx the vertx instance
   * @return the client
   */
  @GenIgnore(GenIgnore.PERMITTED_TYPE)
  static AerospikeClient createNonShared(Vertx vertx) {
    return createNonShared(vertx, new AerospikeConnectOptions());
  }

  /**
   * Determine if we are ready to talk to the database server cluster.
   * @see com.aerospike.client.AerospikeClient#isConnected()
   *
   * @param handler               the handler that will handle response
   */
  void isConnected(Handler<AsyncResult<Boolean>> handler);

  /**
   * Return operating cluster statistics.
   * @see com.aerospike.client.AerospikeClient#getClusterStats()
   *
   * @param handler               the handler that will handle the received cluster statistics
   */
  @GenIgnore(GenIgnore.PERMITTED_TYPE)
  void getClusterStats(Handler<AsyncResult<ClusterStats>> handler);


  /**
   * Close all client connections to database server nodes.
   * @see com.aerospike.client.AerospikeClient#close()
   */
  void close();


  /**
   * Get the underlying {@link com.aerospike.client.AerospikeClient}
   * @return the {@code com.aerospike.client.AerospikeClient} instance which is used internally.
   */
  @GenIgnore(GenIgnore.PERMITTED_TYPE)
  com.aerospike.client.AerospikeClient getAerospikeClient();

  /**
   * Asynchronously write record bin(s).
   * @see com.aerospike.client.AerospikeClient#put(EventLoop, WriteListener, WritePolicy, Key, Bin...) 
   *
   * @param writePolicy           write configuration parameters, pass in null for defaults
   * @param key                   unique record identifier
   * @param bins                  array of bin name/value pairs
   * @param handler               the handler that will handle the result
   * @throws AerospikeException   if event loop registration fails
   */
  @GenIgnore(GenIgnore.PERMITTED_TYPE)
  void put(WritePolicy writePolicy, Key key, Bin[] bins, Handler<AsyncResult<Key>> handler)
      throws AerospikeException;

  /**
   * Asynchronously append bin string values to existing record bin values.
   * @see com.aerospike.client.AerospikeClient#append(EventLoop, WriteListener, WritePolicy, Key, Bin...)
   *
   * @param writePolicy           write configuration parameters, pass in null for defaults
   * @param key                   unique record identifier
   * @param bins                  array of bin name/value pairs
   * @param handler               the handler that will handle the result
   * @throws AerospikeException   if event loop registration fails
   */
  @GenIgnore(GenIgnore.PERMITTED_TYPE)
  void append(WritePolicy writePolicy, Key key, Bin[] bins, Handler<AsyncResult<Key>> handler)
      throws AerospikeException;

  /**
   * Asynchronously prepend bin string values to existing record bin values.
   * @see com.aerospike.client.AerospikeClient#prepend(WritePolicy, Key, Bin...)
   *
   * @param writePolicy           write configuration parameters, pass in null for defaults
   * @param key                   unique record identifier
   * @param bins                  array of bin name/value pairs
   * @param handler               the handler that will handle the result
   * @throws AerospikeException   if event loop registration fails
   */
  @GenIgnore(GenIgnore.PERMITTED_TYPE)
  void prepend(WritePolicy writePolicy, Key key, Bin[] bins, Handler<AsyncResult<Key>> handler)
      throws AerospikeException;

  /**
   * Asynchronously add integer/double bin values to existing record bin values.
   * @see com.aerospike.client.AerospikeClient#add(EventLoop, WriteListener, WritePolicy, Key, Bin...)
   *
   * @param writePolicy           write configuration parameters, pass in null for defaults
   * @param key                   unique record identifier
   * @param bins                  array of bin name/value pairs
   * @param handler               the handler that will handle the result
   * @throws AerospikeException   if event loop registration fails
   */
  @GenIgnore(GenIgnore.PERMITTED_TYPE)
  void add(WritePolicy writePolicy, Key key, Bin[] bins, Handler<AsyncResult<Key>> handler)
      throws AerospikeException;

  /**
   * Asynchronously delete record for specified key.
   * @see com.aerospike.client.AerospikeClient#delete(EventLoop, DeleteListener, WritePolicy, Key)
   *
   * @param writePolicy           write configuration parameters, pass in null for defaults
   * @param key                   unique record identifier
   * @param handler               the handler that will handle the result
   * @throws AerospikeException   if event loop registration fails
   */
  @GenIgnore(GenIgnore.PERMITTED_TYPE)
  void delete(WritePolicy writePolicy, Key key, Handler<AsyncResult<Boolean>> handler)
      throws AerospikeException;

  /**
   * Asynchronously reset record's time to expiration using the policy's expiration.
   * @see com.aerospike.client.AerospikeClient#touch(EventLoop, WriteListener, WritePolicy, Key)
   *
   * @param writePolicy           write configuration parameters, pass in null for defaults
   * @param key                   unique record identifier
   * @param handler               the handler that will handle the result
   * @throws AerospikeException   if event loop registration fails
   */
  @GenIgnore(GenIgnore.PERMITTED_TYPE)
  void touch(WritePolicy writePolicy, Key key, Handler<AsyncResult<Key>> handler)
      throws AerospikeException;


  /**
   * Asynchronously determine if a record key exists.
   * @see com.aerospike.client.AerospikeClient#exists(EventLoop, ExistsListener, Policy, Key)
   *
   * @param policy                generic configuration parameters, pass in null for defaults
   * @param key                   unique record identifier
   * @param handler               the handler that will handle the result
   * @throws AerospikeException   if event loop registration fails
   */
  @GenIgnore(GenIgnore.PERMITTED_TYPE)
  void exists(Policy policy, Key key, Handler<AsyncResult<Boolean>> handler)
      throws AerospikeException;

  /**
   * Asynchronously check if multiple record keys exist in one batch call.
   * @see com.aerospike.client.AerospikeClient#exists(EventLoop, ExistsArrayListener, BatchPolicy, Key[])
   *
   * @param batchPolicy	          batch configuration parameters, pass in null for defaults
   * @param keys				          unique record identifiers
   * @param handler               the handler that will handle the result
   * @throws AerospikeException   if event loop registration fails
   */
  @GenIgnore(GenIgnore.PERMITTED_TYPE)
  void exists(BatchPolicy batchPolicy, Key[] keys, Handler<AsyncResult<List<Boolean>>> handler)
      throws AerospikeException;

  /**
   * Asynchronously read entire record for specified key.
   * @see com.aerospike.client.AerospikeClient#get(EventLoop, RecordListener, Policy, Key)
   *
   * @param policy                generic configuration parameters, pass in null for defaults
   * @param key                   unique record identifier
   * @param handler               the handler that will handle the result
   * @throws AerospikeException   if event loop registration fails
   */
  @GenIgnore(GenIgnore.PERMITTED_TYPE)
  void get(Policy policy, Key key, Handler<AsyncResult<Record>> handler) throws AerospikeException;

  /**
   * Asynchronously read record header and bins for specified key.
   * @see com.aerospike.client.AerospikeClient#get(EventLoop, RecordListener, Policy, Key, String...)
   *
   * @param policy                generic configuration parameters, pass in null for defaults
   * @param key                   unique record identifier
   * @param binNames              bins to retrieve
   * @param handler               the handler that will handle the result
   * @throws AerospikeException   if event loop registration fails
   */
  @GenIgnore(GenIgnore.PERMITTED_TYPE)
  void get(Policy policy, Key key, String[] binNames, Handler<AsyncResult<Record>> handler)
      throws AerospikeException;

  /**
   * Asynchronously read record generation and expiration only for specified key.  Bins are not read.
   * @see com.aerospike.client.AerospikeClient#getHeader(EventLoop, RecordListener, Policy, Key)
   *
   * @param policy                generic configuration parameters, pass in null for defaults
   * @param key                   unique record identifier
   * @param handler               the handler that will handle the result
   * @throws AerospikeException   if event loop registration fails
   */
  @GenIgnore(GenIgnore.PERMITTED_TYPE)
  void getHeader(Policy policy, Key key, Handler<AsyncResult<Record>> handler)
      throws AerospikeException;

  /**
   * Asynchronously read multiple records for specified batch keys in one batch call.
   * @see com.aerospike.client.AerospikeClient#get(EventLoop, BatchListListener, BatchPolicy, List)
   *
   * @param batchPolicy	          batch configuration parameters, pass in null for defaults
   * @param records               list of unique record identifiers and the bins to retrieve.
   *                              The returned records are located in the same list.
   * @param handler               the handler that will handle the result
   * @throws AerospikeException   if event loop registration fails
   */
  @GenIgnore(GenIgnore.PERMITTED_TYPE)
  void get(
      BatchPolicy batchPolicy, List<BatchRead> records, Handler<AsyncResult<List<BatchRead>>> handler)
      throws AerospikeException;

  /**
   * Asynchronously read multiple records for specified keys in one batch call.
   * @see com.aerospike.client.AerospikeClient#get(EventLoop, RecordArrayListener, BatchPolicy, Key[])
   *
   * @param batchPolicy	          batch configuration parameters, pass in null for defaults
   * @param keys                  array of unique record identifiers
   * @param handler               the handler that will handle the result
   * @throws AerospikeException   if event loop registration fails
   */
  @GenIgnore(GenIgnore.PERMITTED_TYPE)
  void get(BatchPolicy batchPolicy, Key[] keys, Handler<AsyncResult<List<Record>>> handler)
      throws AerospikeException;

  /**
   * Asynchronously read multiple record headers and bins for specified keys in one batch call.
   * @see com.aerospike.client.AerospikeClient#get(EventLoop, RecordArrayListener, BatchPolicy, Key[], String...)
   * @param batchPolicy	          batch configuration parameters, pass in null for defaults
   * @param keys                  array of unique record identifiers
   * @param binNames              array of bins to retrieve
   * @param handler               the handler that will handle the result
   * @throws AerospikeException   if event loop registration fails
   */
  @GenIgnore(GenIgnore.PERMITTED_TYPE)
  void get(
      BatchPolicy batchPolicy,
      Key[] keys,
      String[] binNames,
      Handler<AsyncResult<List<Record>>> handler)
      throws AerospikeException;

  /**
   * Asynchronously read multiple record header data for specified keys in one batch call.
   * @see com.aerospike.client.AerospikeClient#getHeader(EventLoop, RecordArrayListener, BatchPolicy, Key[])
   *
   * @param batchPolicy	          batch configuration parameters, pass in null for defaults
   * @param keys                  array of unique record identifiers
   * @param handler               the handler that will handle the result
   * @throws AerospikeException   if event loop registration fails
   */
  @GenIgnore(GenIgnore.PERMITTED_TYPE)
  void getHeader(BatchPolicy batchPolicy, Key[] keys, Handler<AsyncResult<List<Record>>> handler)
      throws AerospikeException;

  /**
   * Asynchronously perform multiple read/write operations on a single key in one batch call.
   * @see com.aerospike.client.AerospikeClient#operate(EventLoop, RecordListener, WritePolicy, Key, Operation...)
   *
   * @param writePolicy				    write configuration parameters, pass in null for defaults
   * @param key					          unique record identifier
   * @param operations			      database operations to perform
   * @param handler               the handler that will handle the result
   * @throws AerospikeException	  if event loop registration fail
   */
  @GenIgnore(GenIgnore.PERMITTED_TYPE)
  void operate(
      WritePolicy writePolicy,
      Key key,
      Operation[] operations,
      Handler<AsyncResult<Record>> handler)
      throws AerospikeException;

  /**
   * Asynchronously read all records in specified namespace and set
   * @see com.aerospike.client.AerospikeClient#scanAll(EventLoop, RecordSequenceListener, ScanPolicy, String, String, String...)
   *
   * @param policy				        scan configuration parameters, pass in null for defaults
   * @param namespace				      namespace - equivalent to database name
   * @param setName				        optional set name - equivalent to database table
   * @param binNames				      optional bins to retrieve. All bins will be returned if empty.
   * @param handler               the handler that will handle the result
   * @throws AerospikeException	  if event loop registration fails
   */
  @GenIgnore(GenIgnore.PERMITTED_TYPE)
  void scanAll(ScanPolicy policy, String namespace, String setName,
               String[] binNames, Handler<AsyncResult<List<KeyRecord>>> handler)
      throws AerospikeException;

  /**
   * Asynchronously read records in specified namespace, set and partition filter.
   * @see com.aerospike.client.AerospikeClient#scanPartitions(EventLoop, RecordSequenceListener, ScanPolicy, PartitionFilter, String, String, String...)
   *
   * @param policy				        scan configuration parameters, pass in null for defaults
   * @param partitionFilter		    filter on a subset of data partitions
   * @param namespace				      namespace - equivalent to database name
   * @param setName				        optional set name - equivalent to database table
   * @param binNames				      optional bins to retrieve. All bins will be returned if empty.
   * @param handler               the handler that will handle the result
   * @throws AerospikeException	  if event loop registration fails
   */
  @GenIgnore(GenIgnore.PERMITTED_TYPE)
  void scanPartitions(ScanPolicy policy, PartitionFilter partitionFilter, String namespace,
                      String setName, String[] binNames, Handler<AsyncResult<List<KeyRecord>>> handler)
      throws AerospikeException;

  /**
   * Asynchronously execute user defined function on server.
   * @see com.aerospike.client.AerospikeClient#execute(EventLoop, ExecuteListener, WritePolicy, Key, String, String, Value...)
   *
   * @param writePolicy				    write configuration parameters, pass in null for defaults
   * @param key					          unique record identifier
   * @param packageName			      server package name where user defined function resides
   * @param functionName			    user defined function
   * @param functionArgs			    arguments passed in to user defined function
   * @param handler               the handler that will handle the result
   * @throws AerospikeException	  if event loop registration fails
   */
  @GenIgnore(GenIgnore.PERMITTED_TYPE)
  void execute(
      WritePolicy writePolicy,
      Key key,
      String packageName,
      String functionName,
      Value[] functionArgs,
      Handler<AsyncResult<Object>> handler)
      throws AerospikeException;

  /**
   * Asynchronously execute query on all server nodes.
   * @see com.aerospike.client.AerospikeClient#query(EventLoop, RecordSequenceListener, QueryPolicy, Statement)
   *
   * @param queryPolicy			      query configuration parameters, pass in null for defaults
   * @param statement				      query filter. Statement instance is not suitable for reuse since it's modified in this method.
   * @param handler               the handler that will handle the result
   * @throws AerospikeException	  if event loop registration fails
   */
  @GenIgnore(GenIgnore.PERMITTED_TYPE)
  void query(
      QueryPolicy queryPolicy, Statement statement, Handler<AsyncResult<List<KeyRecord>>> handler)
      throws AerospikeException;
}
