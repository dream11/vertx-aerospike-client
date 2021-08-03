package io.d11.aerospike.util;

import io.vertx.core.shareddata.LocalMap;
import io.vertx.core.shareddata.Shareable;
import io.vertx.core.Vertx;
import lombok.val;
import java.util.function.Supplier;

public final class SharedDataUtils {
  private static final String SHARED_DATA_MAP_NAME = "__vertx.sharedDataUtils";
  private static final String SHARED_INSTANCE_FORMAT = "__AerospikeClient.__for.__{}:{}";

  public SharedDataUtils() {
  }

  public static String getInstanceName(String host, Integer port) {
    return String.format(SHARED_INSTANCE_FORMAT, host, port);
  }

  public static <T> T getOrCreate(Vertx vertx, String name, Supplier<T> supplier) {
    LocalMap<Object, Object> singletons = vertx.sharedData().getLocalMap(SHARED_DATA_MAP_NAME);
    return (T) ((ThreadSafe)singletons.computeIfAbsent(name, k -> new ThreadSafe(supplier.get()))).getObject();
  }

  public static <T> void removeInstanceByName(Vertx vertx, String name) {
    val singletons = vertx.sharedData().getLocalMap(SHARED_DATA_MAP_NAME);
    singletons.remove(name);
  }

  static class ThreadSafe<T> implements Shareable {
    T object;

    public ThreadSafe(T object) {
      this.object = object;
    }

    public T getObject() {
      return this.object;
    }
  }
}