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
import com.google.common.collect.ImmutableList;
import com.viadeo.kasper.api.component.Domain;
import com.viadeo.kasper.api.component.event.Event;

import java.util.Collection;

import static com.google.common.base.Preconditions.checkNotNull;

public class DomainDescriptor implements KasperComponentDescriptor {

    private final String domainName;
    private final Class<? extends Domain> domainClass;
    private final ImmutableList<QueryHandlerDescriptor> queryHandlerDescriptors;
    private final ImmutableList<CommandHandlerDescriptor> commandHandlerDescriptors;
    private final ImmutableList<RepositoryDescriptor> repositoryDescriptor;
    private final ImmutableList<EventListenerDescriptor> eventListenerDescriptor;
    private final ImmutableList<SagaDescriptor> sagaDescriptor;
    private final ImmutableList<Class<? extends Event>> eventClasses;

    // ------------------------------------------------------------------------

    public DomainDescriptor(final String domainName,
                            final Class<? extends Domain> domainClass,
                            final Collection<QueryHandlerDescriptor> queryHandlerDescriptors,
                            final Collection<CommandHandlerDescriptor> commandHandlerDescriptors,
                            final Collection<RepositoryDescriptor> repositoryDescriptor,
                            final Collection<EventListenerDescriptor> eventListenerDescriptor,
                            final Collection<SagaDescriptor> sagaDescriptor,
                            final Collection<Class<? extends Event>> eventClasses
    ) {
        this.domainName = checkNotNull(domainName);
        this.domainClass = checkNotNull(domainClass);
        this.queryHandlerDescriptors = ImmutableList.copyOf(checkNotNull(queryHandlerDescriptors));
        this.commandHandlerDescriptors = ImmutableList.copyOf(checkNotNull(commandHandlerDescriptors));
        this.repositoryDescriptor = ImmutableList.copyOf(checkNotNull(repositoryDescriptor));
        this.eventListenerDescriptor = ImmutableList.copyOf(checkNotNull(eventListenerDescriptor));
        this.sagaDescriptor = ImmutableList.copyOf(checkNotNull(sagaDescriptor));
        this.eventClasses = ImmutableList.copyOf(checkNotNull(eventClasses));
    }

    // ------------------------------------------------------------------------

    public Class getDomainClass() {
        return domainClass;
    }

    public ImmutableList<QueryHandlerDescriptor> getQueryHandlerDescriptors() {
        return queryHandlerDescriptors;
    }

    public Collection<CommandHandlerDescriptor> getCommandHandlerDescriptors() {
        return commandHandlerDescriptors;
    }

    public Collection<RepositoryDescriptor> getRepositoryDescriptors() {
        return repositoryDescriptor;
    }

    public Collection<EventListenerDescriptor> getEventListenerDescriptors() {
        return eventListenerDescriptor;
    }

    public ImmutableList<SagaDescriptor> getSagaDescriptors() {
        return sagaDescriptor;
    }

    public ImmutableList<Class<? extends Event>> getEventClasses() {
        return eventClasses;
    }

    @Override
    public Class<? extends Domain> getReferenceClass() {
        return domainClass;
    }

    public String getName(){
        return domainName;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(domainName, domainClass, queryHandlerDescriptors, commandHandlerDescriptors, repositoryDescriptor, eventListenerDescriptor, sagaDescriptor, eventClasses);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        final DomainDescriptor other = (DomainDescriptor) obj;
        return Objects.equal(this.domainName, other.domainName) && Objects.equal(this.domainClass, other.domainClass) && Objects.equal(this.queryHandlerDescriptors, other.queryHandlerDescriptors) && Objects.equal(this.commandHandlerDescriptors, other.commandHandlerDescriptors) && Objects.equal(this.repositoryDescriptor, other.repositoryDescriptor) && Objects.equal(this.eventListenerDescriptor, other.eventListenerDescriptor) && Objects.equal(this.sagaDescriptor, other.sagaDescriptor) && Objects.equal(this.eventClasses, other.eventClasses);
    }
}
