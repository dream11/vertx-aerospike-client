package io.d11.aerospike.listeners;

import com.aerospike.client.AerospikeException;
import com.aerospike.client.Key;
import com.aerospike.client.listener.WriteListener;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.impl.ContextInternal;

import java.util.Objects;


public class WriteListenerImpl implements WriteListener {
    final ContextInternal context;
    final Handler<AsyncResult<Key>> handler;

    public WriteListenerImpl(ContextInternal context, Handler<AsyncResult<Key>> handler) {
        Objects.requireNonNull(context, "context must not be null");
        this.context = context;
        this.handler = handler;
    }

    public void onSuccess(Key key) {
        if (handler != null) {
            context.runOnContext((v) -> handler.handle(Future.succeededFuture(key)));
        }
    }

    public void onFailure(AerospikeException e) {
        if (handler != null) {
            context.runOnContext((v) -> handler.handle(Future.failedFuture(e)));
        }
    }
}
