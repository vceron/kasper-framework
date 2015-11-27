// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.platform.bundle.sample2;

import com.viadeo.kasper.api.annotation.XKasperDomain;
import com.viadeo.kasper.api.component.Domain;
import com.viadeo.kasper.api.component.command.Command;
import com.viadeo.kasper.core.component.annotation.XKasperCommandHandler;
import com.viadeo.kasper.core.component.command.AutowiredCommandHandler;

public class SampleDomainBox2 {

    public static class Infra { }

    @XKasperDomain(label = "SampleDomainBox2", prefix = "test", description = "A domain definition used only for the test")
    public static class MyCustomDomain implements Domain { }

    @XKasperCommandHandler(domain = MyCustomDomain.class)
    public static class MyCustomCommandHandler extends AutowiredCommandHandler<Command> {
        public MyCustomCommandHandler(final Infra infra) { }
    }

}