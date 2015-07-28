// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.event.saga.factory;

import com.google.common.base.Optional;
import com.viadeo.kasper.event.saga.Saga;

public interface SagaFactoryProvider {
    SagaFactory getOrCreate(final Saga saga);
    Optional<SagaFactory> get(final Class<? extends Saga> sagaClass);
}