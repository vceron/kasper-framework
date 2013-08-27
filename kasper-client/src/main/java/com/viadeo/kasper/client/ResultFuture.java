// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.client;

import com.sun.jersey.api.client.ClientResponse;

import java.util.concurrent.Future;

abstract class ResultFuture<T> implements Future<T> {

    private final Future<ClientResponse> futureResponse;

    // ------------------------------------------------------------------------

    public ResultFuture(final Future<ClientResponse> futureResponse) {
        this.futureResponse = futureResponse;
    }

    protected Future<ClientResponse> futureResponse() {
        return this.futureResponse;
    }

    // ------------------------------------------------------------------------

    public boolean cancel(final boolean mayInterruptIfRunning) {
        return futureResponse.cancel(mayInterruptIfRunning);
    }

    public boolean isCancelled() {
        return futureResponse.isCancelled();
    }

    public boolean isDone() {
        return futureResponse.isDone();
    }

}
