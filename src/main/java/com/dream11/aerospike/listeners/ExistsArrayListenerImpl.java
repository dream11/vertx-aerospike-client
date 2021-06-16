package com.dream11.aerospike.listeners;

import com.aerospike.client.AerospikeException;
import com.aerospike.client.Key;
import com.aerospike.client.listener.ExistsArrayListener;
import com.google.common.primitives.Booleans;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.impl.ContextInternal;

import java.util.List;
import java.util.Objects;

public class ExistsArrayListenerImpl implements ExistsArrayListener {
    final ContextInternal context;
    final Handler<AsyncResult<List<Boolean>>> handler;

    public ExistsArrayListenerImpl(ContextInternal context, Handler<AsyncResult<List<Boolean>>> handler) {
        Objects.requireNonNull(context, "context must not be null");
        this.context = context;
        this.handler = handler;
    }
    public void onSuccess(Key[] keys, boolean[] b) {
        if (handler != null) {
            context.runOnContext((v) -> handler.handle(Future.succeededFuture(Booleans.asList(b))));
        }
    }

    public void onFailure(AerospikeException e) {
        if (handler != null) {
            context.runOnContext((v) -> handler.handle(Future.failedFuture(e)));
        }
    }
}
