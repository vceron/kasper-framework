// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.component.query.gateway;

import com.viadeo.kasper.api.component.query.Query;
import com.viadeo.kasper.api.component.query.QueryResponse;
import com.viadeo.kasper.api.component.query.QueryResult;
import com.viadeo.kasper.api.context.Context;

/** The Kasper query gateway, used to result queries from the kasper platform */
public interface QueryGateway {

	/**
	 * @param context the query execution context
	 * @param query the query to be resulted
     * @param <RESULT> the query result
	 * @return the Data Transfer Object as an result
     * @throws Exception an exception
	 */
    <RESULT extends QueryResult> QueryResponse<RESULT> retrieve(Query query, Context context) throws Exception;

}