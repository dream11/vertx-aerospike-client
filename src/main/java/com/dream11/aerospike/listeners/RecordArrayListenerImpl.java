package com.dream11.aerospike.listeners;

import com.aerospike.client.AerospikeException;
import com.aerospike.client.Key;
import com.aerospike.client.Record;
import com.aerospike.client.listener.RecordArrayListener;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.impl.ContextInternal;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class RecordArrayListenerImpl implements RecordArrayListener {
    final ContextInternal context;
    final Handler<AsyncResult<List<Record>>> handler;

    public RecordArrayListenerImpl(ContextInternal context, Handler<AsyncResult<List<Record>>> handler) {
        Objects.requireNonNull(context, "context must not be null");
        this.context = context;
        this.handler = handler;
    }

    public void onSuccess(Key[] keys, Record[] records) {
        if (handler != null) {
            context.runOnContext((v) -> handler.handle(Future.succeededFuture(Arrays.asList(records))));
        }
    }

    public void onFailure(AerospikeException e) {
        if (handler != null) {
            context.runOnContext((v) -> handler.handle(Future.failedFuture(e)));
        }
    }
}
