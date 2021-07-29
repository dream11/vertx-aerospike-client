package com.dream11.aerospike.listeners;

import com.aerospike.client.AerospikeException;
import com.aerospike.client.Key;
import com.aerospike.client.listener.ExistsListener;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.impl.ContextInternal;

import java.util.Objects;

public class ExistsListenerImpl implements ExistsListener {
    final ContextInternal context;
    final Handler<AsyncResult<Boolean>> handler;

    public ExistsListenerImpl(ContextInternal context, Handler<AsyncResult<Boolean>> handler) {
        Objects.requireNonNull(context, "context must not be null");
        this.context = context;
        this.handler = handler;
    }
    public void onSuccess(Key key, boolean b) {
        if (handler != null) {
            context.runOnContext((v) -> handler.handle(Future.succeededFuture(b)));
        }
    }

    public void onFailure(AerospikeException e) {
        if (handler != null) {
            context.runOnContext((v) -> handler.handle(Future.failedFuture(e)));
        }
    }
}
