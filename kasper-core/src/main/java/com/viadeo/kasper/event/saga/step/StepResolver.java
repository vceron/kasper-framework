// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.event.saga.step;

import com.viadeo.kasper.event.saga.Saga;
import com.viadeo.kasper.event.saga.SagaIdReconciler;

import java.util.Set;

/**
 * Interface describing the extraction of Steps present in a Saga
 */
public interface StepResolver {

    Set<Step> resolve(Class<? extends Saga> sagaClass, SagaIdReconciler idReconciler);

}