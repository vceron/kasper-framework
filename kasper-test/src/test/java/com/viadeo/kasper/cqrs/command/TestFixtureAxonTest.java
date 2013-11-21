// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.cqrs.command;

import com.viadeo.kasper.KasperID;
import com.viadeo.kasper.context.Context;
import com.viadeo.kasper.context.impl.DefaultContextBuilder;
import com.viadeo.kasper.ddd.IRepository;
import com.viadeo.kasper.ddd.repository.EventSourcedRepository;
import com.viadeo.kasper.ddd.repository.Repository;
import com.viadeo.kasper.impl.DefaultKasperId;
import org.axonframework.test.FixtureConfiguration;
import org.axonframework.test.Fixtures;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static com.viadeo.kasper.cqrs.command.FixtureUseCase.*;
import static com.viadeo.kasper.test.event.EventMatcher.equalToEvent;
import static org.axonframework.test.matchers.Matchers.*;

@RunWith(Parameterized.class)
public class TestFixtureAxonTest {

    private FixtureConfiguration<TestAggregate> fixture;
    private IRepository<TestAggregate> testRepository;

    private static final String firstName = "Richard";
    private static final String lastName = "Stallman";

    // ========================================================================

    @Parameterized.Parameters
    public static Collection repositories() {
        return Arrays.asList(new Object[][] {
                { new TestRepository() },
                { new TestEventRepository() }
        });
    }

    public TestFixtureAxonTest(final IRepository testRepository) {
        if (null != testRepository) {
            this.testRepository = testRepository;
        }
    }

    @Before
    public void resetFixture() {
        this.fixture = Fixtures.newGivenWhenThenFixture(TestAggregate.class);
        fixture.setReportIllegalStateChange(true);

        if (Repository.class.isAssignableFrom(this.testRepository.getClass())) {
            ((Repository) this.testRepository).setEventStore(fixture.getEventStore());
            ((Repository) this.testRepository).setEventBus(fixture.getEventBus());
        }

        // Register the update handler
        final TestChangeLastNameCommandHandler updateHandler = new TestChangeLastNameCommandHandler();
        updateHandler.setRepository(this.testRepository);
        fixture.registerCommandHandler(TestChangeLastNameCommand.class, updateHandler);

        // Register the create handler
        final TestCreateCommandHandler createHandler = new TestCreateCommandHandler();
        createHandler.setRepository(this.testRepository);
        fixture.registerCommandHandler(TestCreateCommand.class, createHandler);
    }

    private Map<String, Object> newContext() {
        final Context context = DefaultContextBuilder.get();
        final Map<String, Object> metaContext = new HashMap<String, Object>() {{
            this.put(Context.METANAME, context);
        }};
        return metaContext;
    }

    // ========================================================================

    @Test
    public void testSimpleCreation() {

        // Given
        final KasperID createId = DefaultKasperId.random();

        // When command is made
        // Then we expect creation and first name changing events
        fixture
                .given()
                .when(
                    new TestCreateCommand(
                        createId,
                        firstName
                    ),
                    newContext()
                )
                .expectReturnValue(CommandResponse.ok())
                .expectEventsMatching(payloadsMatching(exactSequenceOf(
                    equalToEvent(new TestCreatedEvent(createId)),
                    equalToEvent(new TestFirstNameChangedEvent(firstName)),
                    andNoMore()
                )));

    }

    // ------------------------------------------------------------------------

    @Test
    public void testSimpleUpdateAfterCreateCommand() {

        // Given
        final KasperID aggregateId = DefaultKasperId.random();

        // When command is made, Then we expect creation and first name changing events
        fixture
                .givenCommands(
                    new TestCreateCommand(
                        aggregateId,
                        firstName
                    )
                )
                .when(
                    new TestChangeLastNameCommand(
                        aggregateId,
                        lastName
                    ),
                    newContext()
                )
                .expectReturnValue(CommandResponse.ok())
                .expectEventsMatching(payloadsMatching(exactSequenceOf(
                    equalToEvent(new TestLastNameChangedEvent(lastName)),
                    andNoMore()
                )));

    }

    // ------------------------------------------------------------------------

    @Test
    public void testSimpleUpdateAfterCreateEvents() {

        /**
         * Non-event sourced repositories cannot handle "given" events
         */
        if ( ! EventSourcedRepository.class.isAssignableFrom(this.testRepository.getClass())) {
            return;
        }

        // Given
        final KasperID aggregateId = DefaultKasperId.random();

        // When command is made, Then we expect creation and first name changing events
        fixture
                .given(
                    new TestCreatedEvent(aggregateId),
                    new TestFirstNameChangedEvent(firstName)
                )
                .when(
                    new TestChangeLastNameCommand(
                        aggregateId,
                        lastName
                    ),
                    newContext()
                )
                .expectReturnValue(CommandResponse.ok())
                .expectEventsMatching(payloadsMatching(exactSequenceOf(
                    equalToEvent(new TestLastNameChangedEvent(lastName)),
                    andNoMore()
                )));

    }

}
