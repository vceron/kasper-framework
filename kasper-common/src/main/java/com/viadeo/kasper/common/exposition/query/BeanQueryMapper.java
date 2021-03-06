// ----------------------------------------------------------------------------
//  This file is part of the Kasper framework.
//
//  The Kasper framework is free software: you can redistribute it and/or 
//  modify it under the terms of the GNU Lesser General Public License as 
//  published by the Free Software Foundation, either version 3 of the 
//  License, or (at your option) any later version.
//
//  Kasper framework is distributed in the hope that it will be useful,
//  but WITHOUT ANY WARRANTY; without even the implied warranty of
//  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//  GNU Lesser General Public License for more details.
//
//  You should have received a copy of the GNU Lesser General Public License
//  along with the framework Kasper.  
//  If not, see <http://www.gnu.org/licenses/>.
// --
//  Ce fichier fait partie du framework logiciel Kasper
//
//  Ce programme est un logiciel libre ; vous pouvez le redistribuer ou le 
//  modifier suivant les termes de la GNU Lesser General Public License telle 
//  que publiée par la Free Software Foundation ; soit la version 3 de la 
//  licence, soit (à votre gré) toute version ultérieure.
//
//  Ce programme est distribué dans l'espoir qu'il sera utile, mais SANS 
//  AUCUNE GARANTIE ; sans même la garantie tacite de QUALITÉ MARCHANDE ou 
//  d'ADÉQUATION à UN BUT PARTICULIER. Consultez la GNU Lesser General Public 
//  License pour plus de détails.
//
//  Vous devez avoir reçu une copie de la GNU Lesser General Public License en 
//  même temps que ce programme ; si ce n'est pas le cas, consultez 
//  <http://www.gnu.org/licenses>
// ----------------------------------------------------------------------------
// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.common.exposition.query;

import com.google.common.collect.ImmutableSet;
import com.viadeo.kasper.api.component.query.Query;
import com.viadeo.kasper.common.exposition.TypeAdapter;

import java.util.*;

import static com.google.common.base.Preconditions.checkNotNull;

class BeanQueryMapper implements TypeAdapter<Query> {

    private final Set<PropertyAdapter> adapters;
    private final BeanConstructor queryCtr;

    // ------------------------------------------------------------------------

    public BeanQueryMapper(final BeanConstructor queryCtr, final Set<PropertyAdapter> adapters) {
        this.adapters = ImmutableSet.copyOf(sortPropertyAdapterSet(checkNotNull(adapters)));
        this.queryCtr = checkNotNull(queryCtr);
    }

    // ------------------------------------------------------------------------

    @Override
    public void adapt(final Query value, final QueryBuilder builder) throws Exception {
        for (final PropertyAdapter adapter : adapters) {
            adapter.adapt(value, builder);
        }
    }

    @Override
    public Query adapt(final QueryParser parser) throws Exception {
        final Object[] ctrParams = new Object[queryCtr.parameters().size()];
        final List<PropertyAdapterPair<PropertyAdapter, Object>> valuesToSet = new ArrayList<PropertyAdapterPair<PropertyAdapter, Object>>();

        for (final PropertyAdapter adapter : adapters) {
            /*
             * we have to check if the property exists in th sream if it
             * doesn't we should not override it in case of setters (for the
             * ctr we have no choice as we can't pass null to primitive
             * args)
             */
            final boolean exists = adapter.existsInQuery(parser);
            final Object value = adapter.adapt(parser);
            final BeanConstructorProperty ctrParam = queryCtr.parameters().get(adapter.getName());

            if (null != ctrParam) {
                ctrParams[ctrParam.position()] = value;
            } else {
                if (exists) {
                    valuesToSet.add(
                            new PropertyAdapterPair<PropertyAdapter, Object>(adapter, value)
                    );
                }
            }
        }

        final Object queryInstance = queryCtr.create(ctrParams);
        for (final PropertyAdapterPair<PropertyAdapter, Object> pair : valuesToSet) {
            pair.firstValue().mutate(queryInstance, pair.secondValue());
        }

        return (Query) queryInstance;
    }

    private SortedSet<PropertyAdapter> sortPropertyAdapterSet(final Set<PropertyAdapter> propertyAdapters) {
        final SortedSet<PropertyAdapter> sorted = new TreeSet<PropertyAdapter>(new Comparator<PropertyAdapter>() {
            @Override
            public int compare(PropertyAdapter o1, PropertyAdapter o2) {
                return o1.getName().compareTo(o2.getName());
            }
        });
        for (PropertyAdapter propertyAdapter : propertyAdapters) {
            sorted.add(propertyAdapter);
        }
        return sorted;
    }

}
