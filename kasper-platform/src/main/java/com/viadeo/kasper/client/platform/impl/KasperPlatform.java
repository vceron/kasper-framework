// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================

package com.viadeo.kasper.client.platform.impl;

import com.viadeo.kasper.client.platform.OldPlatform;
import com.viadeo.kasper.client.platform.components.eventbus.KasperEventBus;
import com.viadeo.kasper.client.platform.configuration.PlatformConfiguration;
import com.viadeo.kasper.context.Context;
import com.viadeo.kasper.context.impl.DefaultContextBuilder;
import com.viadeo.kasper.core.boot.AnnotationRootProcessor;
import com.viadeo.kasper.core.boot.ComponentsInstanceManager;
import com.viadeo.kasper.cqrs.command.Command;
import com.viadeo.kasper.cqrs.command.CommandGateway;
import com.viadeo.kasper.cqrs.query.Query;
import com.viadeo.kasper.cqrs.query.QueryGateway;
import com.viadeo.kasper.cqrs.query.QueryResponse;
import com.viadeo.kasper.cqrs.query.QueryResult;
import com.viadeo.kasper.event.IEvent;
import org.axonframework.domain.GenericEventMessage;

import java.util.HashMap;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * The default implementation for the Kasper platform
 *
 * @deprecated use {@link com.viadeo.kasper.client.platform.impl.DefaultPlatform} instead.
 */
@Deprecated
public class KasperPlatform implements OldPlatform {

    /** The platform configuration **/
    protected PlatformConfiguration platformConfiguration;

    /** The platform components **/
    protected CommandGateway commandGateway;
    protected QueryGateway queryGateway;
    protected AnnotationRootProcessor rootProcessor;
    protected KasperEventBus eventBus;

    private volatile Boolean booted = false;

    private static final Lock LOCK = new Lock();
    private static class Lock { }

    // ------------------------------------------------------------------------

    @Override
    public void boot() {
        synchronized (LOCK) {
            if (!booted) {
                this.rootProcessor.boot();
                booted = true;
            }
        }
    }

    @Override
    public boolean isBooted() {
        return booted;
    }

    // ------------------------------------------------------------------------

    @Override
    public void setCommandGateway(final CommandGateway commandGateway) {
        this.commandGateway = checkNotNull(commandGateway);
    }

    @Override
    public CommandGateway getCommandGateway() {
        return this.commandGateway;
    }

    @Override
    public void sendCommand(final Command command, final Context context) throws Exception {
        this.commandGateway.sendCommand(checkNotNull(command), checkNotNull(context));
    }

    // ------------------------------------------------------------------------

    @Override
    public AnnotationRootProcessor getRootProcessor() {
        return this.rootProcessor;
    }

    @Override
    public void setRootProcessor(final AnnotationRootProcessor rootProcessor) {
        this.rootProcessor = checkNotNull(rootProcessor);
    }

    @Override
    public ComponentsInstanceManager getComponentsInstanceManager() {
        return this.rootProcessor.getComponentsInstanceManager();
    }

    // ------------------------------------------------------------------------

    @Override
    public void setQueryGateway(final QueryGateway queryGateway) {
        this.queryGateway = checkNotNull(queryGateway);
    }

    @Override
    public QueryGateway getQueryGateway() {
        return this.queryGateway;
    }

    @Override
    public <RESULT extends QueryResult> QueryResponse<RESULT> retrieve(final Query query, final Context context) throws Exception {
        return this.queryGateway.retrieve(checkNotNull(query), checkNotNull(context));
    }

    // ------------------------------------------------------------------------

    @Override
    public void setEventBus(final KasperEventBus eventBus) {
        this.eventBus = checkNotNull(eventBus);
    }

    @Override
    public KasperEventBus getEventBus() {
        return this.eventBus;
    }

    @Override
    public void publishEvent(final IEvent event) {
        this.publishEvent(DefaultContextBuilder.get(), checkNotNull(event));
    }

    @Override
    public void publishEvent(final Context context, final IEvent event) {
        this.eventBus.publish(
                new GenericEventMessage<>(
                    checkNotNull(event),
                    new HashMap<String, Object>() {{
                        this.put(Context.METANAME, context);
                    }}
                )
        );
    }

}
