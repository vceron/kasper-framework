// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.domain.sample.hello.command.handler;

import com.google.common.base.Optional;
import com.viadeo.kasper.api.component.command.CommandResponse;
import com.viadeo.kasper.api.response.CoreReasonCode;
import com.viadeo.kasper.core.component.annotation.XKasperCommandHandler;
import com.viadeo.kasper.core.component.command.AutowiredEntityCommandHandler;
import com.viadeo.kasper.domain.sample.hello.api.HelloDomain;
import com.viadeo.kasper.domain.sample.hello.api.command.DeleteHelloCommand;
import com.viadeo.kasper.domain.sample.hello.command.entity.Hello;
import com.viadeo.kasper.domain.sample.hello.command.repository.HelloRepository;

import javax.inject.Inject;

/**
 * Implementing EntityCommandHandler instead of CommandHandler provides a simpler way
 * to access a sticked repository and a uow-safe event bus
 *
 * EntityCommandHandler provides a base implementation
 *
 */
@XKasperCommandHandler( /* Required annotation to define the sticked domain */
        domain = HelloDomain.class,
        description = "Change the buddy name of an existing hello message"
)
public class DeleteHelloCommandHandler extends AutowiredEntityCommandHandler<DeleteHelloCommand, Hello> {

    private HelloRepository repository;

    @Inject
    public DeleteHelloCommandHandler(HelloRepository repository) {
        this.repository = repository;
    }

    @Override
    public CommandResponse handle(final DeleteHelloCommand command) {
        /**
         * Load the entity as we plan to modify it
         */
        Optional<Hello> helloOptional = repository.load(command.getId());

        if (!helloOptional.isPresent()) {
            return CommandResponse.error(CoreReasonCode.NOT_FOUND, "Supplied HelloWorld id cannot be found");
        }

        final Hello hello = helloOptional.get();

        /**
         * Mutate the aggregate
         */
        hello.delete();

        /**
         * Once loaded by the repository an entity will be automagically saved
         * after handling
         */
        return CommandResponse.ok();
    }

}
