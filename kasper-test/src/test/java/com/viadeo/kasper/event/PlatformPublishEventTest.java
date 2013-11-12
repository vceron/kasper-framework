// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.event;

import com.viadeo.kasper.AbstractPlatformTests;
import com.viadeo.kasper.KasperID;
import com.viadeo.kasper.KasperTestIdGenerator;
import com.viadeo.kasper.context.impl.DefaultContextBuilder;
import com.viadeo.kasper.ddd.Domain;
import com.viadeo.kasper.ddd.annotation.XKasperDomain;
import com.viadeo.kasper.er.annotation.XKasperConcept;
import com.viadeo.kasper.er.impl.AbstractRootConcept;
import com.viadeo.kasper.event.annotation.XKasperEvent;
import com.viadeo.kasper.event.annotation.XKasperEventListener;
import com.viadeo.kasper.event.domain.impl.AbstractEntityEvent;
import com.viadeo.kasper.event.impl.AbstractEventListener;
import org.joda.time.DateTime;
import org.junit.Test;

import java.util.concurrent.locks.ReentrantLock;

import static org.junit.Assert.assertTrue;

public class PlatformPublishEventTest extends AbstractPlatformTests {

	private static final ReentrantLock LOCK = new ReentrantLock();
	private static boolean received = false;

	// ------------------------------------------------------------------------

	@XKasperDomain(label = "testDomain", prefix = "tst", description = "test domain")
	public static class TestDomain implements Domain {}

    @XKasperConcept(label = "test root concept", domain = TestDomain.class)
    public static class TestRootConcept extends AbstractRootConcept {}

	@SuppressWarnings("serial")
	@XKasperEvent(action = "test")
	public static class TestEvent extends AbstractEntityEvent<TestDomain> {
		public TestEvent(final KasperID idShortMessage, final DateTime creationDate) {
			super(DefaultContextBuilder.get(), idShortMessage, 1L, creationDate);
		}
	}

	@XKasperEventListener( domain = TestDomain.class )
	public static class TestListener extends AbstractEventListener<TestEvent> {
		@Override
		public void handle(final EventMessage<TestEvent> eventMessage) {
			received = true;
		}
	}

	// ------------------------------------------------------------------------

	@Test
	public void testPublishEvent() throws InterruptedException {
        // Given
		final KasperID id = KasperTestIdGenerator.get();
		final Event event = new TestEvent(id, new DateTime());
		event.setContext(this.newContext());

        // When
		this.getPlatform().publishEvent(event);
        Thread.sleep(3000);

        // Then
		assertTrue(received);
	}

}
