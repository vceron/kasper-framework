// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.locators.impl;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.*;
import com.viadeo.kasper.core.locators.QueryServicesLocator;
import com.viadeo.kasper.cqrs.query.*;
import com.viadeo.kasper.cqrs.query.annotation.XKasperQueryCache;
import com.viadeo.kasper.cqrs.query.exceptions.KasperQueryException;
import com.viadeo.kasper.cqrs.query.impl.QueryFilterProcessor;
import com.viadeo.kasper.cqrs.query.impl.QueryServiceProcessor;
import com.viadeo.kasper.ddd.Domain;
import com.viadeo.kasper.tools.ReflectionGenericsResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;
import static java.util.Collections.unmodifiableCollection;

/**
 * Base implementation for query services locator
 */
public class DefaultQueryServicesLocator implements QueryServicesLocator {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultQueryServicesLocator.class);

    private static final Collection<ServiceFilter> EMPTY_FILTERS =
            unmodifiableCollection(new ArrayList<ServiceFilter>());

    /**
     * Registered services and filters
     */
    @SuppressWarnings("rawtypes")
    private final ClassToInstanceMap<QueryService> services = MutableClassToInstanceMap.create();
    private final ClassToInstanceMap<ServiceFilter> filters = MutableClassToInstanceMap.create();
    private final Map<Class<? extends QueryService>, Class<? extends Domain>> serviceDomains = Maps.newHashMap();

    /**
     * Global filters *
     */
    private final List<Class<? extends ServiceFilter>> globalFilters = Lists.newArrayList();

    /**
     * Registered query classes and associated service instances
     */
    private final Map<Class<? extends Query>, QueryService<?, ?>> serviceQueryClasses = newHashMap();

    /**
     * Registered services names and associated service instances
     */
    @SuppressWarnings("rawtypes")
    private final Map<String, QueryService> serviceNames = newHashMap();

    /**
     * Association of filters per service and domains *
     */
    private final Map<Class<? extends QueryService<?, ?>>, List<Class<? extends ServiceFilter>>> appliedFilters = newHashMap();
    private final Map<Class<? extends QueryService<?, ?>>, List<ServiceFilter>> instanceFilters = newHashMap();
    private final Map<Class<? extends ServiceFilter>, Class<? extends Domain>> isDomainSticky = Maps.newHashMap();

    // ------------------------------------------------------------------------

    @SuppressWarnings("rawtypes")
    @Override
    public void registerService(final String name, final QueryService<?, ?> service, final Class<? extends Domain> domainClass) {
        checkNotNull(name);
        checkNotNull(service);

        if (name.isEmpty()) {
            throw new KasperQueryException("Name of services cannot be empty : " + service.getClass());
        }

        final Class<? extends QueryService> serviceClass = service.getClass();

        @SuppressWarnings("unchecked") // Safe
        final Optional<Class<? extends Query>> optQueryClass =
                (Optional<Class<? extends Query>>)
                        ReflectionGenericsResolver.getParameterTypeFromClass(
                                service.getClass(), QueryService.class, QueryService.PARAMETER_QUERY_POSITION);

        if (!optQueryClass.isPresent()) {
            throw new KasperQueryException("Unable to find query class for service " + service.getClass());
        }

        final Class<? extends Query> queryClass = optQueryClass.get();
        if (this.serviceQueryClasses.containsKey(queryClass)) {
            throw new KasperQueryException("A service for the same query class is already registered : " + queryClass);
        }

        if (this.serviceNames.containsKey(name)) {
            throw new KasperQueryException("A service by the same name is already registered : " + name);
        }

        this.serviceQueryClasses.put(queryClass, service);
        this.serviceNames.put(name, service);
        this.services.put(serviceClass, service);
        this.serviceDomains.put(serviceClass, domainClass);
    }

    // ------------------------------------------------------------------------

    /* Filter name is not currently used in the locator */
    @Override
    public void registerFilter(final String name, final ServiceFilter queryFilter, final boolean isGlobal, final Class<? extends Domain> stickyDomainClass) {
        checkNotNull(name);
        checkNotNull(queryFilter);

        if (name.isEmpty()) {
            throw new KasperQueryException("Name of service filters cannot be empty : " + queryFilter.getClass());
        }

        final Class<? extends ServiceFilter> filterClass = queryFilter.getClass();
        this.filters.put(filterClass, queryFilter);

        if (isGlobal) {
            this.globalFilters.add(filterClass);
            this.instanceFilters.clear(); // Drop all service instances caches
            if (null != stickyDomainClass) {
                this.isDomainSticky.put(queryFilter.getClass(), stickyDomainClass);
            }
        }

    }

    @Override
    public void registerFilter(final String name, final ServiceFilter queryFilter, boolean isGlobal) {
        this.registerFilter(name, queryFilter, isGlobal, null);
    }

    @Override
    public void registerFilter(final String name, final ServiceFilter queryFilter) {
        this.registerFilter(name, queryFilter, false, null);
    }

    // ------------------------------------------------------------------------

    @Override
    public void registerFilterForService(final Class<? extends QueryService<?, ?>> queryServiceClass, final Class<? extends ServiceFilter> filterClass) {
        checkNotNull(queryServiceClass);
        checkNotNull(filterClass);

        final List<Class<? extends ServiceFilter>> serviceFilters;

        if (!this.appliedFilters.containsKey(queryServiceClass)) {
            serviceFilters = newArrayList();
            this.appliedFilters.put(queryServiceClass, serviceFilters);
        } else if (!this.appliedFilters.get(queryServiceClass).contains(filterClass)) {
            serviceFilters = this.appliedFilters.get(queryServiceClass);
        } else {
            serviceFilters = null;
        }

        if (null != serviceFilters) {
            serviceFilters.add(filterClass);
            this.instanceFilters.remove(queryServiceClass); // Drop cache of instances
        }
    }

    // ------------------------------------------------------------------------

    @SuppressWarnings("rawtypes")
    @Override
    public Optional<QueryService> getServiceFromClass(final Class<? extends QueryService<?, ?>> serviceClass) {
        final QueryService service = this.services.getInstance(serviceClass);
        return Optional.fromNullable(service);
    }

    @SuppressWarnings("rawtypes")
    @Override
    public Optional<QueryService> getServiceByName(final String serviceName) {
        final QueryService service = this.serviceNames.get(serviceName);
        return Optional.fromNullable(service);
    }

    @Override
    @SuppressWarnings("rawtypes")
    public Optional<QueryService> getServiceFromQueryClass(final Class<? extends Query> queryClass) {
        final QueryService service = this.serviceQueryClasses.get(queryClass);
        return Optional.fromNullable(service);
    }

    @Override
    public Optional<RequestProcessorChain<Query, QueryResult<QueryPayload>>> getRequestProcessorChain(Class<? extends Query> queryClass) {
        Optional<QueryService> optionalQS = getServiceFromQueryClass(queryClass);
        if (optionalQS.isPresent()) {
            QueryService<Query, QueryPayload> qs = optionalQS.get();

            Collection<ServiceFilter> serviceFilters = getFiltersForServiceClass((Class<? extends QueryService<?, ?>>) qs.getClass());

            List<RequestProcessor<Query, QueryResult<QueryPayload>>> requestProcessors = Lists.newArrayList(filtersProcessor(serviceFilters), new QueryServiceProcessor<Query, QueryPayload>(qs));

            return Optional.of(RequestProcessorChain.makeChain(requestProcessors));
        }
        return Optional.absent();  //To change body of implemented methods use File | Settings | File Templates.
    }


    @SuppressWarnings("rawtypes")
    QueryFilterProcessor<Query, QueryPayload> filtersProcessor(final Collection<ServiceFilter> serviceFilters) {

        Collection<QueryFilter> queryFilters = Lists.newArrayList(Iterables.filter(serviceFilters, QueryFilter.class));
        Collection<ResultFilter> resultFilters = Lists.newArrayList(Iterables.filter(serviceFilters, ResultFilter.class));

        return new QueryFilterProcessor(queryFilters, resultFilters);
    }

    @Override
    public Collection<QueryService<?, ?>> getServices() {
        return unmodifiableCollection(this.serviceQueryClasses.values());
    }

    // ------------------------------------------------------------------------

    @Override
    public Collection<ServiceFilter> getFiltersForServiceClass(final Class<? extends QueryService<?, ?>> serviceClass) {

        // Ensure service has filters
        if (!this.appliedFilters.containsKey(serviceClass) && this.globalFilters.isEmpty()) {
            return EMPTY_FILTERS;
        }


        // Ensure instances has been collected, lazy loading
        if (!this.instanceFilters.containsKey(serviceClass)) {
            List<Class<? extends ServiceFilter>> filtersToApply = this.appliedFilters.get(serviceClass);

            if (null == filtersToApply) {
                filtersToApply = Lists.newArrayList();
            }

            // Apply required global filters
            for (final Class<? extends ServiceFilter> globalFilterClass : this.globalFilters) {
                if (this.isDomainSticky.containsKey(globalFilterClass)) {
                    final Class<? extends Domain> stickyDomainClass = this.isDomainSticky.get(globalFilterClass);
                    if ((null != stickyDomainClass) && stickyDomainClass.equals(this.serviceDomains.get(serviceClass))) {
                        filtersToApply.add(globalFilterClass);
                    }
                } else {
                    filtersToApply.add(globalFilterClass);
                }
            }

            // Copy required filters instances to this service cache
            final List<ServiceFilter> instances = newArrayList();
            for (Class<? extends ServiceFilter> filterClass : Sets.newHashSet(filtersToApply)) {
                if (this.filters.containsKey(filterClass)) {
                    instances.add(this.filters.get(filterClass));
                } else {
                    LOGGER.error(String.format("Service %s asks to be filtered, but no instance of filter %s can be found in records",
                            serviceClass, filterClass));
                }
            }
            this.instanceFilters.put(serviceClass, instances);
        }

        // Return the filter instances
        return unmodifiableCollection(this.instanceFilters.get(serviceClass));
    }

}
