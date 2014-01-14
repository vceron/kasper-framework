// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.cqrs.query.interceptor;

import com.google.common.base.Optional;
import com.google.common.reflect.TypeToken;
import com.viadeo.kasper.core.interceptor.Interceptor;
import com.viadeo.kasper.core.interceptor.InterceptorChain;
import com.viadeo.kasper.core.interceptor.QueryInterceptorFactory;
import com.viadeo.kasper.cqrs.query.Query;
import com.viadeo.kasper.cqrs.query.QueryResponse;
import com.viadeo.kasper.cqrs.query.QueryResult;
import com.viadeo.kasper.security.SecurityConfiguration;

import static com.google.common.base.Preconditions.checkNotNull;

public class QuerySecurityInterceptorFactory extends QueryInterceptorFactory {
    private SecurityConfiguration securityConfiguration;

    // ------------------------------------------------------------------------

    public QuerySecurityInterceptorFactory(final SecurityConfiguration securityConfiguration) {
        this.securityConfiguration = checkNotNull(securityConfiguration);
    }

    @Override
    public Optional<InterceptorChain<Query, QueryResponse<QueryResult>>> create(final TypeToken<?> type) {
        final Interceptor<Query, QueryResponse<QueryResult>> interceptor =
            new QuerySecurityInterceptor<>(securityConfiguration);
        return Optional.of(InterceptorChain.makeChain(interceptor));
    }

}