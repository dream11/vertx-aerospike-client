package com.dream11.aerospike.listeners;

import com.aerospike.client.AerospikeException;
import com.aerospike.client.Key;
import com.aerospike.client.Record;
import com.aerospike.client.listener.RecordListener;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.impl.ContextInternal;

import java.util.Objects;


public class RecordListenerImpl implements RecordListener {
    final ContextInternal context;
    final Handler<AsyncResult<Record>> handler;

    public RecordListenerImpl(ContextInternal context, Handler<AsyncResult<Record>> handler) {
        Objects.requireNonNull(context, "context must not be null");
        this.context = context;
        this.handler = handler;
    }

    public void onSuccess(Key key, Record record) {
        if (handler != null) {
            context.runOnContext((v) -> handler.handle(Future.succeededFuture(record)));
        }
    }

    public void onFailure(AerospikeException e) {
        if (handler != null) {
            context.runOnContext((v) -> handler.handle(Future.failedFuture(e)));
        }
    }

}
