// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.event.domain;

import com.viadeo.kasper.ddd.Domain;

/**
 *
 * A Kasper event related to an entity update
 *
 */
public interface EntityUpdatedEvent<D extends Domain> extends EntityEvent<D> {

}


