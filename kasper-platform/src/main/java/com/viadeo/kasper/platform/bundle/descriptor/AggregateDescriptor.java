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
package com.viadeo.kasper.platform.bundle.descriptor;

import com.google.common.base.Objects;
import com.google.common.collect.Lists;
import com.viadeo.kasper.api.component.event.Event;
import com.viadeo.kasper.core.component.command.aggregate.Concept;
import com.viadeo.kasper.core.component.command.aggregate.ddd.AggregateRoot;

import java.util.Collection;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

public class AggregateDescriptor implements KasperComponentDescriptor {

    private final Class<? extends AggregateRoot> aggregateClass;
    private final Class<? extends Concept> sourceClass;
    private final Class<? extends Concept> targetClass;
    private final Collection<Class<? extends Event>> sourceEventClasses;

    // ------------------------------------------------------------------------

    public AggregateDescriptor(final Class<? extends AggregateRoot> aggregateClass,
                               final List<Class<? extends Event>> eventClasses) {
        this(checkNotNull(aggregateClass), null, null, checkNotNull(eventClasses));
    }

    public AggregateDescriptor(final Class<? extends AggregateRoot> aggregateClass,
                               final Class<? extends Concept> sourceClass,
                               final Class<? extends Concept> targetClass,
                               final List<Class<? extends Event>> sourceEventClasses) {
        this.aggregateClass = checkNotNull(aggregateClass);
        this.sourceClass = sourceClass;
        this.targetClass = targetClass;
        this.sourceEventClasses = Lists.newArrayList(checkNotNull(sourceEventClasses));
    }

    // ------------------------------------------------------------------------

    public boolean isRelation() {
        return (null != sourceClass) && (null != targetClass);
    }

    @Override
    public Class<? extends AggregateRoot> getReferenceClass() {
        return aggregateClass;
    }

    public Class<? extends Concept> getSourceClass() {
        return sourceClass;
    }

    public Class<? extends Concept> getTargetClass() {
        return targetClass;
    }

    public Collection<Class<? extends Event>> getSourceEventClasses() {
        return sourceEventClasses;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(aggregateClass, sourceClass, targetClass, sourceEventClasses);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        final AggregateDescriptor other = (AggregateDescriptor) obj;
        return Objects.equal(this.aggregateClass, other.aggregateClass) && Objects.equal(this.sourceClass, other.sourceClass) && Objects.equal(this.targetClass, other.targetClass) && Objects.equal(this.sourceEventClasses, other.sourceEventClasses);
    }
}
