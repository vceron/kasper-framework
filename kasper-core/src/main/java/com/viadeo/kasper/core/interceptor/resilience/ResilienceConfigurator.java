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
package com.viadeo.kasper.core.interceptor.resilience;

import com.google.common.collect.Maps;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigException;

import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

public class ResilienceConfigurator {

    private final Config config;
    private final InputConfig defaultInputConfig;
    private final Map<String,InputConfig> configByInputName;

    // ------------------------------------------------------------------------

    public ResilienceConfigurator(final Config config) {
        this.config = checkNotNull(config);
        this.configByInputName = Maps.newHashMap();
        this.defaultInputConfig = new InputConfig(
                config.getBoolean("runtime.hystrix.circuitBreaker.enable"),
                config.getInt("runtime.hystrix.circuitBreaker.requestVolumeThreshold"),
                config.getInt("runtime.hystrix.circuitBreaker.thresholdInPercent"),
                config.getInt("runtime.hystrix.circuitBreaker.sleepWindowInMillis"),
                config.getInt("runtime.hystrix.execution.timeoutInMillis"),
                config.getInt("runtime.hystrix.threadPool.coreSize"),
                config.getInt("runtime.hystrix.threadPool.queueSizeRejectionThreshold")
        );
    }

    // ------------------------------------------------------------------------

    public boolean isHystrixMetricEnable() {
        return getBooleanOr(config, "runtime.hystrix.metricsPublisher.enable", Boolean.TRUE);
    }

    // ------------------------------------------------------------------------

    public InputConfig configure(final Object input) {
        checkNotNull(input);

        InputConfig inputConfig = configByInputName.get(input.getClass().getName());

        if (inputConfig == null) {
            try {
                final Config config = this.config.getConfig(
                        "runtime.hystrix.input." + input.getClass().getSimpleName()
                );

                inputConfig = new InputConfig(
                        getBooleanOr(config, "circuitBreaker.enable", defaultInputConfig.circuitBreakerEnable),
                        getIntOr(config, "circuitBreaker.requestVolumeThreshold", defaultInputConfig.circuitBreakerRequestVolumeThreshold),
                        getIntOr(config, "circuitBreaker.thresholdInPercent", defaultInputConfig.circuitBreakerThresholdInPercent),
                        getIntOr(config, "circuitBreaker.sleepWindowInMillis", defaultInputConfig.circuitBreakerSleepWindowInMillis),
                        getIntOr(config, "execution.timeoutInMillis", defaultInputConfig.executionTimeoutInMillis),
                        getIntOr(config, "threadPool.coreSize", defaultInputConfig.threadPoolCoreSize),
                        getIntOr(config, "threadPool.queueSizeRejectionThreshold", defaultInputConfig.threadPoolQueueSizeRejectionThreshold)
                );

            } catch (final ConfigException e) {
                inputConfig = defaultInputConfig;
            }
            configByInputName.put(input.getClass().getName(), inputConfig);
        }

        return inputConfig;
    }

    protected Boolean getBooleanOr(final Config config, final String path, final Boolean defaultValue) {
        try {
            return config.getBoolean(path);
        } catch (final ConfigException e) {
            return defaultValue;
        }
    }

    protected Integer getIntOr(Config config, String path, Integer defaultValue) {
        try {
            return config.getInt(path);
        } catch (final ConfigException e) {
            return defaultValue;
        }
    }

    public static class InputConfig {

        /**
         * True ic the circuit breaker is enable, false otherwise.
         */
        public final Boolean circuitBreakerEnable;

        /**
         * The minimum of requests volume allowing to define a statistical window that will be compare to
         * <code>circuitBreakerThresholdInPercent</code>.
         */
        public final Integer circuitBreakerRequestVolumeThreshold;

        /**
         * The percent of 'marks' that must be failed to trip the circuit.
         */
        public final Integer circuitBreakerThresholdInPercent;

        /**
         * The window time in milliseconds after tripping circuit before allowing retry.
         */
        public final Integer circuitBreakerSleepWindowInMillis;

        /**
         * The delay for which we consider an execution as timed out.
         */
        public final Integer executionTimeoutInMillis;

        /**
         *  Core thread-pool size
         */
        public final Integer threadPoolCoreSize;

        /**
         *  Queue size rejection threshold is an artificial "max" size at which rejections will occur even if max queue size has not been reached
         */
        public final Integer threadPoolQueueSizeRejectionThreshold;

        public InputConfig(
                final Boolean circuitBreakerEnable,
                final Integer circuitBreakerRequestVolumeThreshold,
                final Integer circuitBreakerThresholdInPercent,
                final Integer circuitBreakerSleepWindowInMillis,
                final Integer executionTimeoutInMillis,
                final Integer threadPoolCoreSize,
                final Integer threadPoolQueueSizeRejectionThreshold
        ) {
            this.circuitBreakerEnable = circuitBreakerEnable;
            this.circuitBreakerRequestVolumeThreshold = circuitBreakerRequestVolumeThreshold;
            this.circuitBreakerThresholdInPercent = circuitBreakerThresholdInPercent;
            this.circuitBreakerSleepWindowInMillis = circuitBreakerSleepWindowInMillis;
            this.executionTimeoutInMillis = executionTimeoutInMillis;
            this.threadPoolCoreSize = threadPoolCoreSize;
            this.threadPoolQueueSizeRejectionThreshold = threadPoolQueueSizeRejectionThreshold;
        }
    }

}
