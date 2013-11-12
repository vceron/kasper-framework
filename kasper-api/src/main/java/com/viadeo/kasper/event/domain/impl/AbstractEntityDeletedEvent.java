// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.event.domain.impl;

import com.viadeo.kasper.KasperID;
import com.viadeo.kasper.context.Context;
import com.viadeo.kasper.ddd.Domain;
import com.viadeo.kasper.event.domain.EntityDeletedEvent;
import org.joda.time.DateTime;

/**
 *
 * Base implementation for deleted entity events
 *
 * @see com.viadeo.kasper.event.domain.EntityEvent
 */
public abstract class AbstractEntityDeletedEvent<D extends Domain>
        extends AbstractEntityEvent<D>
        implements EntityDeletedEvent<D>
{

    protected AbstractEntityDeletedEvent(final KasperID id,
                                         final Long version,
                                         final DateTime lastModificationDate) {
        super(id, version, lastModificationDate);
    }

    protected AbstractEntityDeletedEvent(final Context context,
                                         final KasperID id,
                                         final Long version,
                                         final DateTime lastModificationDate) {
        super(context, id, version, lastModificationDate);
    }

}
