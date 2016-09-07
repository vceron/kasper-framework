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
package com.viadeo.kasper.core.resolvers;

import com.google.common.base.Optional;
import com.viadeo.kasper.api.component.Domain;
import com.viadeo.kasper.api.component.event.DomainEvent;
import com.viadeo.kasper.api.component.event.Event;
import com.viadeo.kasper.api.exception.KasperException;
import com.viadeo.kasper.core.component.annotation.XKasperUnregistered;
import org.junit.Test;

import static org.junit.Assert.*;

public class EventResolverTest {

    @XKasperUnregistered
    private static class TestDomain implements Domain {}

    @XKasperUnregistered
    private static class TestEvent implements Event { }

    @XKasperUnregistered
    private static class TestDomainEvent implements DomainEvent<TestDomain> { }

    @XKasperUnregistered
    private static class TestGenericDomainEvent implements DomainEvent { }

    // ------------------------------------------------------------------------

    @Test
    public void testGetDomainFromDomainEvent() {
        // Given
        final EventResolver resolver = new EventResolver();

        // When
        final Optional<Class<? extends Domain>> domain =
                resolver.getDomainClass(TestDomainEvent.class);

        // Then
        assertTrue(domain.isPresent());
        assertEquals(TestDomain.class, domain.get());
    }

    @Test
    public void testGetDomainFromGenericDomainEvent() {
        // Given
        final EventResolver resolver = new EventResolver();

        // When
        try {
            final Optional<Class<? extends Domain>> domain =
                    resolver.getDomainClass(TestGenericDomainEvent.class);
            fail();
        } catch (final KasperException e) {
            // Then should raise exception
        }
    }

    @Test
    public void testGetDomainFromEvent() {
        // Given
        final EventResolver resolver = new EventResolver();

        // When
        final Optional<Class<? extends Domain>> domain =
                resolver.getDomainClass(TestEvent.class);

        // Then
        assertFalse(domain.isPresent());
    }

}
