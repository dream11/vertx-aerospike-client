package com.dream11.aerospike.util;

import io.vertx.core.shareddata.LocalMap;
import io.vertx.core.shareddata.Shareable;
import io.vertx.reactivex.core.Vertx;
import java.util.function.Supplier;

public final class SharedDataUtils {
  private static final String SHARED_DATA_MAP_NAME = "__vertx.sharedDataUtils";

  public SharedDataUtils() {
  }

  public static <T> T getOrCreate(Vertx vertx, String name, Supplier<T> supplier) {
    LocalMap<Object, Object> singletons = vertx.getDelegate().sharedData().getLocalMap(SHARED_DATA_MAP_NAME);
    return (T) ((ThreadSafe)singletons.computeIfAbsent(name, k -> new ThreadSafe(supplier.get()))).getObject();
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