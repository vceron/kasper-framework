// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.cqrs.util;

import com.viadeo.kasper.context.Context;
import org.slf4j.MDC;

import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

public final class MDCUtils {

    private MDCUtils() { /* Utility class */ }

    @SuppressWarnings("unchecked")
    public static void enrichMdcContextMap(final Context context) {
        checkNotNull(context);

        final Map initialContextMap = MDC.getCopyOfContextMap();
        final Map contextMapEnrichedWithContext = context.asMap(initialContextMap);
        MDC.setContextMap(contextMapEnrichedWithContext);
    }

}