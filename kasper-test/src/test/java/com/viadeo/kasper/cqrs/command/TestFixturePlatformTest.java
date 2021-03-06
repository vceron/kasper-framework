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
package com.viadeo.kasper.cqrs.command;

import com.viadeo.kasper.api.id.DefaultKasperId;
import com.viadeo.kasper.api.id.KasperID;
import com.viadeo.kasper.api.response.CoreReasonCode;
import com.viadeo.kasper.api.response.KasperReason;
import com.viadeo.kasper.test.platform.KasperMatcher;
import com.viadeo.kasper.test.platform.KasperPlatformFixture;
import org.axonframework.commandhandling.interceptors.JSR303ViolationException;
import org.axonframework.test.AxonAssertionError;
import org.junit.Before;
import org.junit.Test;

import static com.viadeo.kasper.cqrs.command.FixtureUseCase.*;
import static com.viadeo.kasper.test.platform.KasperMatcher.anyKasperId;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class TestFixturePlatformTest {

    protected KasperPlatformFixture fixture;

    private static final String firstName = "Richard";
    private static final String lastName = "Stallman";

    // ========================================================================

    @Before
    public void resetFixture() {
        this.fixture = new KasperPlatformFixture().register(FixtureUseCase.getDomainBundle());
    }

    // ========================================================================

    @Test
    public void testSimpleCreation() {

        final KasperID createId = DefaultKasperId.random();

        fixture
            .given()
            .when(
                new TestCreateCommand(
                    createId,
                    firstName
                )
            )
            .expectReturnOK()
            .expectExactSequenceOfEvents(
                new TestCreatedEvent(createId),
                new TestFirstNameChangedEvent(firstName)
            );

    }

    @Test
    public void testSimpleUnexpectedValidation() {

        final KasperID createId = DefaultKasperId.random();

        try {
            fixture
                .given()
                .when(
                    new TestCreateCommand(
                        createId,
                        null
                    )
                )
                .expectReturnOK();
            fail();
        } catch (final RuntimeException e) {
            assertEquals(JSR303ViolationException.class, e.getCause().getClass());
        }

    }

    // ------------------------------------------------------------------------

    @Test
    public void testSimpleExpectedValidationOnCommand() {

        final KasperID createId = DefaultKasperId.random();

        fixture
            .given()
            .when(
                new TestCreateCommand(
                    createId,
                    null
                )
            )
            .expectValidationErrorOnField("firstName");
    }

    // ------------------------------------------------------------------------

    @Test
    public void testSimpleExpectedValidationOnQuery() {
        fixture
            .given()
            .when(
                new TestQuery(null)
            )
            .expectValidationErrorOnField("type");
    }

    // ------------------------------------------------------------------------

    @Test(expected = AxonAssertionError.class)
    public void testSimpleExpectedValidationOnBadField() {
        final KasperID createId = DefaultKasperId.random();

        fixture
                .given()
                .when(
                    new TestCreateCommand(
                        createId,
                        null
                    )
                )
                .expectValidationErrorOnField("foo");

    }

    // ------------------------------------------------------------------------

    @Test
    public void testSimpleUpdateAfterCreateCommand() {

        final KasperID aggregateId = DefaultKasperId.random();

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
                )
            )
            .expectReturnOK()
            .expectExactSequenceOfEvents(
                new TestLastNameChangedEvent(lastName)
            );
    }

    @Test
    public void testSimpleCommandError() {
        fixture
                .given()
                .when(
                        new FixtureUseCase.TestCommand("ERROR")
                )
                .expectReturnError(
                        new KasperReason("ERROR", "I'm bad")
                );
    }

    @Test
    public void testSimpleCommandErrorWithCodeReasonCode() {
        fixture
                .given()
                .when(new FixtureUseCase.TestCoreReasonCodeCommand(CoreReasonCode.CONFLICT))
                .expectReturnError(CoreReasonCode.CONFLICT);
    }

    @Test
    public void testSimpleQueryOK() {
        fixture
            .given()
            .when(
                new TestQuery("OK")
            )
            .expectReturnResponse(
                new TestResult("42")
            );
    }

    @Test
    public void testSimpleQueryError() {
        fixture
            .given()
            .when(
                new TestQuery("ERROR")
            )
            .expectReturnError(
                new KasperReason("ERROR", "I'm bad")
            );
    }

    @Test
    public void testSimpleQueryErrorWithCodeReasonCode() {
        fixture
                .given()
                .when(new TestCoreReasonCodeQuery(CoreReasonCode.CONFLICT))
                .expectReturnError(CoreReasonCode.CONFLICT);
    }

    @Test
    public void testSimpleQueryRefused() {
        fixture
            .given()
            .when(
                new TestQuery("REFUSED")
            )
            .expectReturnRefused(
                new KasperReason("REFUSED", "Go To Hell")
            );
    }

    @Test
    public void testTwoTimesOrMoreSimpleQueryRefused() {
        // 1rst time
        fixture
                .given()
                .when(
                        new TestQuery("REFUSED")
                )
                .expectReturnRefused(
                        new KasperReason("REFUSED", "Go To Hell")
                );

        // 2nd time
        fixture
                .given()
                .when(
                        new TestQuery("REFUSED")
                )
                .expectReturnRefused(
                        new KasperReason("REFUSED", "Go To Hell")
                );
    }

    @SuppressWarnings("deprecation")
    @Test
    public void testEventNotificationOk() {
        fixture
                .given()
                .when(new TestCreatedEvent(
                        new com.viadeo.kasper.api.id.StringKasperId("one"))
                )
                .expectEventNotificationOn(
                        TestCreatedEventListener.class
                );
    }

    @Test
    public void testEventNotificationAfterCommandOk() {
        fixture
                .given()
                .when(
                        new TestCreateCommand(
                                DefaultKasperId.random(),
                                firstName
                        )
                )
                .expectEventNotificationOn(
                        TestCreatedEventListener.class,
                        TestFirstNameChangedEventListener.class
                );
    }

    @Test
    public void testZeroEventNotificationAfterCommandOk() {
        fixture
                .given()
                .when(
                        new TestCommand("OK")
                )
                .expectZeroEventNotification();
    }

    @Test
    public void testCommandDelegationAfterCommandOk() {
        final DefaultKasperId kasperId = DefaultKasperId.random();

        fixture
                .given()
                .when(
                        new TestCreateUserCommand(kasperId, "Jack", "Bauer")
                )
                .expectExactSequenceOfCommands(
                        new TestCreateCommand(kasperId, "Jack"),
                        new TestChangeLastNameCommand(kasperId, "Bauer")
                );
    }

    @Test
    public void testCommandDelegationAfterCommandUsingAnyKasperIdOk() {
        fixture
                .given()
                .when(
                        new TestCreateUserCommand(DefaultKasperId.random(), "Jack", "Bauer")
                )
                .expectExactSequenceOfCommands(
                        new TestCreateCommand(anyKasperId(), "Jack"),
                        new TestChangeLastNameCommand(anyKasperId(), "Bauer")
                );
    }

    @Test
    public void testCommandDelegationAfterEventOk() {

        fixture
                .given()
                .when(
                        new DoSyncUserEvent("Jack", "Bauer")
                )
                .expectExactSequenceOfCommands(
                        new TestCreateCommand(anyKasperId(), "Jack"),
                        new TestChangeLastNameCommand(anyKasperId(), "Bauer")
                );
    }
}
