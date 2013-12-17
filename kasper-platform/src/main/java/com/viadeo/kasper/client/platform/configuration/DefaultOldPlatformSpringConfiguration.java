// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.client.platform.configuration;

import com.codahale.metrics.MetricRegistry;
import com.viadeo.kasper.client.platform.components.eventbus.KasperEventBus;
import com.viadeo.kasper.client.platform.impl.OldKasperPlatform;
import com.viadeo.kasper.core.boot.*;
import com.viadeo.kasper.core.locators.DomainLocator;
import com.viadeo.kasper.core.locators.QueryHandlersLocator;
import com.viadeo.kasper.core.metrics.KasperMetrics;
import com.viadeo.kasper.core.resolvers.*;
import com.viadeo.kasper.cqrs.command.CommandGateway;
import com.viadeo.kasper.cqrs.command.RepositoryManager;
import com.viadeo.kasper.cqrs.query.QueryGateway;
import org.axonframework.commandhandling.CommandBus;
import org.axonframework.unitofwork.UnitOfWorkFactory;
import org.springframework.context.annotation.Bean;

@Deprecated
public class DefaultOldPlatformSpringConfiguration extends DefaultOldPlatformConfiguration {

    private ComponentsInstanceManager instancesManager;

    @Bean
    @Override
    public ComponentsInstanceManager getComponentsInstanceManager() {
        if (null != this.instancesManager) {
            return this.instancesManager;
        } else {
            final SpringComponentsInstanceManager sman = new SpringComponentsInstanceManager();
            this.instancesManager = sman;
            return sman;
        }
    }

    // ------------------------------------------------------------------------

    @Bean
    @Override
    public AnnotationRootProcessor annotationRootProcessor(final ComponentsInstanceManager instancesManager){
        return super.annotationRootProcessor(instancesManager);
    }

    @Bean(initMethod = "boot")
    @Override
    public OldKasperPlatform kasperPlatform(final CommandGateway commandGateway
            , final QueryGateway queryGateway
            , final KasperEventBus eventBus
            , final AnnotationRootProcessor annotationRootProcessor
    ) {
        return super.kasperPlatform(commandGateway, queryGateway, eventBus, annotationRootProcessor);
    }

    @Bean
    @Override
    public KasperEventBus eventBus(){
        return super.eventBus();
    }

    @Bean
    @Override
    public CommandGateway commandGateway(final CommandBus commandBus) {
        return super.commandGateway(commandBus);
    }

    @Bean
    @Override
    public CommandBus commandBus(final UnitOfWorkFactory uowFactory){
        return super.commandBus(uowFactory);
    }

    @Bean
    @Override
    public UnitOfWorkFactory uowFactory() {
        return super.uowFactory();
    }

    @Bean
    @Override
    public RepositoryManager repositoryManager() {
        return super.repositoryManager();
    }

    @Bean
    @Override
    public DomainLocator domainLocator(final CommandHandlerResolver commandHandlerResolver, final RepositoryResolver repositoryResolver) {
        return super.domainLocator(commandHandlerResolver, repositoryResolver);
    }

    @Bean
    @Override
    public QueryHandlersLocator queryHandlersLocator(final QueryHandlerResolver queryHandlerResolver) {
        return super.queryHandlersLocator(queryHandlerResolver);
    }

    @Bean
    @Override
    public CommandHandlersProcessor commandHandlersProcessor(final CommandBus commandBus, final DomainLocator domainLocator,
                                                             final RepositoryManager repositoryManager, final KasperEventBus eventBus,
                                                             final CommandHandlerResolver commandHandlerResolver ){
        return super.commandHandlersProcessor(commandBus, domainLocator, repositoryManager, eventBus, commandHandlerResolver);
    }

    @Bean
    @Override
    public RepositoriesProcessor repositoriesProcessor(final RepositoryManager repositoryManager, final KasperEventBus eventBus){
        return super.repositoriesProcessor(repositoryManager, eventBus);
    }

    @Bean
    @Override
    public QueryHandlerAdaptersProcessor queryHandlerAdaptersProcessor(QueryHandlersLocator locator) {
        return super.queryHandlerAdaptersProcessor(locator);
    }

    @Bean
    @Override
    public EventListenersProcessor eventListenersProcessor(final KasperEventBus eventBus, final CommandGateway commandGateway) {
        return super.eventListenersProcessor(eventBus, commandGateway);
    }

    @Bean
    @Override
    public QueryHandlersProcessor queryHandlersProcessor(final QueryHandlersLocator locator){
        return super.queryHandlersProcessor(locator);
    }

    @Bean
    @Override
    public DomainsProcessor domainsProcessor(final DomainLocator domainLocator){
        return super.domainsProcessor(domainLocator);
    }

    @Bean
    @Override
    public QueryGateway queryGateway(final QueryHandlersLocator locator){
        return super.queryGateway(locator);
    }

    // ------------------------------------------------------------------------

    @Bean
    @Override
    public CommandHandlerResolver commandHandlerResolver(final DomainResolver domainResolver) {
        return super.commandHandlerResolver(domainResolver);
    }

    @Bean
    @Override
    public DomainResolver domainResolver() {
        return super.domainResolver();
    }

    @Bean
    @Override
    public CommandResolver commandResolver(
            final DomainLocator domainLocator,
            final DomainResolver domainResolver,
            final CommandHandlerResolver commandHandlerResolver
    ) {
        return super.commandResolver(domainLocator, domainResolver, commandHandlerResolver);
    }

    @Bean
    @Override
    public EventListenerResolver eventListenerResolver(
            final DomainResolver domainResolver
    ) {
        return super.eventListenerResolver(domainResolver);
    }

    @Bean
    @Override
    public QueryResolver queryResolver(
            final DomainResolver domainResolver,
            final QueryHandlerResolver queryHandlerResolver,
            final QueryHandlersLocator queryHandlersLocator
    ) {
        return super.queryResolver(domainResolver, queryHandlerResolver, queryHandlersLocator);
    }

    @Bean
    @Override
    public QueryResultResolver queryResultResolver(
            final DomainResolver domainResolver,
            final QueryHandlerResolver queryHandlerResolver,
            final QueryHandlersLocator queryHandlersLocator
    ) {
        return super.queryResultResolver(domainResolver, queryHandlerResolver, queryHandlersLocator);
    }

    @Bean
    @Override
    public QueryHandlerResolver queryHandlerResolver(final DomainResolver domainResolver) {
        return super.queryHandlerResolver(domainResolver);
    }

    @Bean
    @Override
    public RepositoryResolver repositoryResolver(final EntityResolver entityResolver, final DomainResolver domainResolver) {
        return super.repositoryResolver(entityResolver, domainResolver);
    }

    @Bean
    @Override
    public EntityResolver entityResolver(
            final ConceptResolver conceptResolver,
            final RelationResolver relationResolver,
            final DomainResolver domainResolver
    ) {
        return super.entityResolver(conceptResolver, relationResolver, domainResolver);
    }

    @Bean
    @Override
    public ConceptResolver conceptResolver(final DomainResolver domainResolver) {
        return super.conceptResolver(domainResolver);
    }

    @Bean
    @Override
    public RelationResolver relationResolver(final DomainResolver domainResolver, final ConceptResolver conceptResolver)  {
        return super.relationResolver(domainResolver, conceptResolver);
    }

    @Bean
    @Override
    public EventResolver eventResolver(final DomainResolver domainResolver) {
        return super.eventResolver(domainResolver);
    }

    @Bean
    @Override
    public ResolverFactory resolverFactory(
            final DomainResolver domainResolver,
            final CommandResolver commandResolver,
            final CommandHandlerResolver commandHandlerResolver,
            final EventListenerResolver eventListenerResolver,
            final QueryResolver queryResolver,
            final QueryResultResolver queryResultResolver,
            final QueryHandlerResolver queryHandlerResolver,
            final RepositoryResolver repositoryResolver,
            final EntityResolver entityResolver,
            final ConceptResolver conceptResolver,
            final RelationResolver relationResolver,
            final EventResolver eventResolver
    ) {
        return super.resolverFactory(
                domainResolver, commandResolver,
                commandHandlerResolver, eventListenerResolver,
                queryResolver, queryResultResolver, queryHandlerResolver,
                repositoryResolver, entityResolver,
                conceptResolver, relationResolver, eventResolver);
    }

    @Bean
    public KasperMetrics initKasperMetrics(final MetricRegistry metricRegistry, final ResolverFactory resolverFactory) {
        KasperMetrics.setResolverFactory(resolverFactory);
        KasperMetrics.setMetricRegistry(metricRegistry);
        return KasperMetrics.instance();
    }

    @Bean
    @Override
    public MetricRegistry metricRegistry(){
        return super.metricRegistry();
    }

}