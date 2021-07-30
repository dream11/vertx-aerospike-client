package io.d11.aerospike.listeners;

import com.aerospike.client.AerospikeException;
import com.aerospike.client.Key;
import com.aerospike.client.listener.ExecuteListener;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.impl.ContextInternal;

import java.util.Objects;

public class ExecuteListenerImpl implements ExecuteListener {
    final ContextInternal context;
    final Handler<AsyncResult<Object>> handler;

    public ExecuteListenerImpl(ContextInternal context, Handler<AsyncResult<Object>> handler) {
        Objects.requireNonNull(context, "context must not be null");
        this.context = context;
        this.handler = handler;
    }

    public void onSuccess(Key key, Object obj) {
        if (handler != null) {
            context.runOnContext((v) -> handler.handle(Future.succeededFuture(obj)));
        }
    }

    public void onFailure(AerospikeException e) {
        if (handler != null) {
            context.runOnContext((v) -> handler.handle(Future.failedFuture(e)));
        }
    }
}
