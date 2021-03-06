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
package com.viadeo.kasper.spring.platform;

import com.codahale.metrics.MetricRegistry;
import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.typesafe.config.Config;
import com.viadeo.kasper.core.component.command.gateway.CommandGateway;
import com.viadeo.kasper.core.component.command.gateway.KasperCommandGateway;
import com.viadeo.kasper.core.component.event.eventbus.KasperEventBus;
import com.viadeo.kasper.core.component.query.gateway.KasperQueryGateway;
import com.viadeo.kasper.core.component.query.gateway.QueryGateway;
import com.viadeo.kasper.platform.ExtraComponent;
import com.viadeo.kasper.platform.Meta;
import com.viadeo.kasper.platform.builder.PlatformContext;
import com.viadeo.kasper.platform.bundle.sample.MyCustomDomainBox;
import com.viadeo.kasper.platform.configuration.KasperPlatformConfiguration;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.swing.text.DateFormatter;
import javax.swing.text.DefaultFormatter;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

public class SpringDomainBundleUTest {

    private static PlatformContext platformPlatformContext;

    @Configuration
    public static class FakeConfiguration {
        @Bean
        public MyCustomDomainBox.MyCustomCommandHandler myCustomCommandHandler(){
            return new MyCustomDomainBox.MyCustomCommandHandler();
        }
    }

    // ------------------------------------------------------------------------

    @BeforeClass
    public static void setup() {
        platformPlatformContext = new PlatformContext(
            mock(Config.class),
            mock(KasperEventBus.class),
            mock(CommandGateway.class),
            mock(QueryGateway.class),
            mock(MetricRegistry.class),
            Lists.<ExtraComponent>newArrayList(),
            mock(Meta.class)
        );
    }

    @Test
    public void configure_shouldBeOk() {
        // Given
        final SpringDomainBundle springDomainBundle = new SpringDomainBundle(
            new MyCustomDomainBox.MyCustomDomain(),
            Lists.<Class>newArrayList()
        );

        // When
        springDomainBundle.configure(platformPlatformContext);

        // Then throws no exception
    }

    @Test
    public void configure_withConfiguration_shouldBeAccessibleThroughDomainContext() {
        // Given
        final SpringDomainBundle springDomainBundle = new SpringDomainBundle(
            new MyCustomDomainBox.MyCustomDomain(),
            Lists.<Class>newArrayList(FakeConfiguration.class)
        );

        // When
        springDomainBundle.configure(platformPlatformContext);

        // Then
        final Optional<MyCustomDomainBox.MyCustomCommandHandler> commandHandlerOptional =
                springDomainBundle.get(MyCustomDomainBox.MyCustomCommandHandler.class);
        assertNotNull(commandHandlerOptional);
        assertTrue(commandHandlerOptional.isPresent());
    }

    @Test
    public void configure_withNamedBean_shouldBeAccessibleThroughDomainContext() {
        // Given
        final String beanName = "hihihi";
        final MyCustomDomainBox.MyCustomEventListener expectedEventListener = new MyCustomDomainBox.MyCustomEventListener();
        final SpringDomainBundle springDomainBundle = new SpringDomainBundle(
            new MyCustomDomainBox.MyCustomDomain(),
            Lists.<Class>newArrayList(),
            new SpringDomainBundle.BeanDescriptor(beanName, expectedEventListener)
        );

        // When
        springDomainBundle.configure(platformPlatformContext);

        // Then

        final Optional<MyCustomDomainBox.MyCustomEventListener> eventListenerOptional =
                springDomainBundle.get(MyCustomDomainBox.MyCustomEventListener.class);
        assertNotNull(eventListenerOptional);
        assertTrue(eventListenerOptional.isPresent());
        assertEquals(expectedEventListener, eventListenerOptional.get());
    }

    @Test
    public void configure_withBean_shouldBeAccessibleThroughDomainContext() {
        // Given
        final DateFormatter dateFormatter = new DateFormatter();
        final SpringDomainBundle springDomainBundle = new SpringDomainBundle(
            new MyCustomDomainBox.MyCustomDomain(),
            Lists.<Class>newArrayList(),
            new SpringDomainBundle.BeanDescriptor(DefaultFormatter.class, dateFormatter)
        );

        // When
        springDomainBundle.configure(platformPlatformContext);

        // Then
        final Optional<DefaultFormatter> formatterOptional = springDomainBundle.get(DefaultFormatter.class);
        assertNotNull(formatterOptional);
        assertTrue(formatterOptional.isPresent());
        assertEquals(dateFormatter, formatterOptional.get());

        final Optional<DateFormatter> formatterOptional2 = springDomainBundle.get(DateFormatter.class);
        assertNotNull(formatterOptional2);
        assertTrue(formatterOptional2.isPresent());
        assertEquals(dateFormatter, formatterOptional2.get());
    }

    @Test
    public void configure_withComponentsDefinedInTheBuilderContext_shouldBeAccessibleThroughDomainContext() {
        // Given
        final SpringDomainBundle springDomainBundle = new SpringDomainBundle(
            new MyCustomDomainBox.MyCustomDomain(),
            Lists.<Class>newArrayList()
        );

        final ExecutorService workers = Executors.newFixedThreadPool(2);

        final List<ExtraComponent> extraComponents = Lists.newArrayList();
        extraComponents.add(new ExtraComponent("workers", ExecutorService.class, workers));

        final KasperPlatformConfiguration platformConfiguration = new KasperPlatformConfiguration();

        final PlatformContext platformContext = new PlatformContext(
                platformConfiguration.configuration(),
                platformConfiguration.eventBus(),
                platformConfiguration.commandGateway(),
                platformConfiguration.queryGateway(),
                platformConfiguration.metricRegistry(),
                extraComponents,
                Meta.UNKNOWN
        );

        // When
        springDomainBundle.configure(platformContext);

        // Then
        assertEquals(workers, springDomainBundle.get(ExecutorService.class).get());
        assertEquals(platformConfiguration.eventBus(), springDomainBundle.get(KasperEventBus.class).get());
        assertEquals(platformConfiguration.commandGateway(), springDomainBundle.get(KasperCommandGateway.class).get());
        assertEquals(platformConfiguration.queryGateway(), springDomainBundle.get(KasperQueryGateway.class).get());
        assertEquals(platformConfiguration.configuration(), springDomainBundle.get(Config.class).get());
        assertEquals(platformConfiguration.metricRegistry(), springDomainBundle.get(MetricRegistry.class).get());
    }

}
