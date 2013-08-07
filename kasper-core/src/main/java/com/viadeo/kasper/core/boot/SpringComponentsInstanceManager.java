// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================

package com.viadeo.kasper.core.boot;

import com.google.common.base.Optional;
import com.viadeo.kasper.exception.KasperException;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * Base implementation for a components instance manager based on the current Spring context
 */
public class SpringComponentsInstanceManager implements ComponentsInstanceManager, ApplicationContextAware {

    /**
     * The injected Spring context (ApplicationContextAware)
     */
    private ApplicationContext context;

    /**
     * Set to true if the manager must not create instances
     * and expects the beans to be present in the current context
     */
    private boolean beansMustExists = false;

    private static final String ALREADY_REGISTERED = "An instance of %s has already been recorded !";
    private static final String NO_BEAN_FOUND = "No bean found in context for class %s";

    // ------------------------------------------------------------------------

    /**
     * Retrieve a Spring bean by class
     *
     * Just try to get the bean from class using the current context
     *
     * If no bean is found, then a new simple bean is created with
     * autowiring capabilities
     *
     * @param clazz the class for which you have to provide an instance
     * @return the corresponding bean (optional)
     */
    @Override
    public <E> Optional<E> getInstanceFromClass(final Class<? extends E> clazz) {
        E objInstance = null;

        try {

            objInstance = context.getBean(clazz);

        } catch (final NoSuchBeanDefinitionException e) {

            if (!this.beansMustExists) {
                final ConfigurableBeanFactory cfb = (ConfigurableBeanFactory) this.context.getAutowireCapableBeanFactory();
                objInstance = ((AutowireCapableBeanFactory) cfb).createBean(clazz);
                cfb.registerSingleton(clazz.getSimpleName(), objInstance);
            } else {
                throw new KasperException(String.format(NO_BEAN_FOUND, clazz), e);
            }

        }

        return Optional.fromNullable(objInstance);
    }

    /**
     * Record a new bean
     *
     * If a bean already exists with the supplied class or the real class of the object
     * then a KasperException (runtime) is thrown.
     *
     * Otherwise, the instance is autowired and registered to the current Spring context
     *
     * @param clazz the class to use for recording the instance
     * @param objInstance the instance to be recorded
     */
    @Override
    public void recordInstance(final Class<?> clazz, final Object objInstance) {
        try {

            /* Try with the supplied class */
            context.getBean(clazz);
            throw new KasperException(String.format(ALREADY_REGISTERED, clazz));

        } catch (final NoSuchBeanDefinitionException e) {

            try {

                /* Try with the real class of the instance */
                if (!clazz.equals(objInstance.getClass())) {
                    context.getBean(objInstance.getClass());
                    throw new KasperException(String.format(ALREADY_REGISTERED, objInstance.getClass()));
                } else {
                    throw new NoSuchBeanDefinitionException("");
                }

            } catch (final NoSuchBeanDefinitionException e2) {
                final ConfigurableBeanFactory cfb = (ConfigurableBeanFactory) this.context.getAutowireCapableBeanFactory();
                ((AutowireCapableBeanFactory) cfb).autowireBean(objInstance);
                cfb.registerSingleton(clazz.getSimpleName(), objInstance);
            }
        }
    }

    // ------------------------------------------------------------------------

    @Override
    public void setApplicationContext(final ApplicationContext context) throws BeansException {
        this.context = context;
    }

    public void setBeansMustExists(final boolean flag) {
        this.beansMustExists = flag;
    }

}