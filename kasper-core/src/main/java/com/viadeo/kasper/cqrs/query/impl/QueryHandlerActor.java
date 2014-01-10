// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.cqrs.query.impl;

import com.codahale.metrics.Timer;
import com.viadeo.kasper.context.Context;
import com.viadeo.kasper.cqrs.RequestActor;
import com.viadeo.kasper.cqrs.RequestActorsChain;
import com.viadeo.kasper.cqrs.query.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.viadeo.kasper.core.metrics.KasperMetrics.getMetricRegistry;
import static com.viadeo.kasper.core.metrics.KasperMetrics.name;

public class QueryHandlerActor<Q extends Query, RESULT extends QueryResult> implements RequestActor<Q, QueryResponse<RESULT>> {

    private static final Logger LOGGER = LoggerFactory.getLogger(QueryHandlerActor.class);
    private static final String GLOBAL_TIMER_REQUESTS_TIME_NAME = name(QueryHandlerActor.class, "requests-time");

    private final QueryHandler<Q, RESULT> queryHandler;

    // ------------------------------------------------------------------------

    public QueryHandlerActor(final QueryHandler<Q, RESULT> queryHandler) {
        this.queryHandler = queryHandler;
    }

    // ------------------------------------------------------------------------

    @Override
    public QueryResponse<RESULT> process(final Q query, final Context context,
                                        final RequestActorsChain<Q, QueryResponse<RESULT>> chain) throws Exception {
        /* Call the handler */
        Exception exception = null;
        QueryResponse<RESULT> ret = null;

        final Timer.Context classTimer = getMetricRegistry().timer(GLOBAL_TIMER_REQUESTS_TIME_NAME).time();
        final Timer.Context timer = getMetricRegistry().timer(name(query.getClass(), "requests-time")).time();

        final QueryMessage<Q> message = new QueryMessage<Q>(context, query);

        try {
            try {

                LOGGER.info("Call handler " + queryHandler.getClass().getSimpleName());
                ret = queryHandler.retrieve(message);

            } catch (final UnsupportedOperationException e) {
                if (QueryHandler.class.isAssignableFrom(queryHandler.getClass())) {
                    ret = (QueryResponse<RESULT>) ((QueryHandler) queryHandler).retrieve(message.getQuery());
                } else {
                    timer.close();
                    classTimer.close();
                    throw e;
                }
            }
        } catch (final RuntimeException e) {
            exception = e;
        } catch (final Exception e) {
            exception = e;
        }

        /* Monitor the request calls */
        timer.stop();
        classTimer.stop();

        if (null != exception) {
            throw exception;
        }

        return ret;
    }

}