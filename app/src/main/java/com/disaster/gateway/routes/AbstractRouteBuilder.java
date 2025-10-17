package com.disaster.gateway.routes;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;

/**
 * An abstract base class for defining Apache Camel routes with built-in support for:
 * <ul>
 *     <li>Automatic timing of route execution using {@code intercept()}</li>
 *     <li>Route invocation logging with {@code interceptFrom()}</li>
 *     <li>Execution duration logging via {@code onCompletion()}</li>
 * </ul>
 *
 * Concrete subclasses should override {@link #configure()} to define route logic,
 * and must implement {@link #getLogger()} to provide a route-specific logger.
 *
 * This class helps eliminate repetitive logging and timing logic by centralizing
 * them into a reusable route configuration.
 */
public abstract class AbstractRouteBuilder extends RouteBuilder {

    /**
     * Configures interceptors and completion processors that apply to all routes
     * defined by subclasses.
     *
     * - {@code intercept()}: Sets a start time at the beginning of every processor in all routes.
     * - {@code interceptFrom()}: Logs when the route starts.
     * - {@code onCompletion()}: Logs the total time taken by the route after completion.
     */
    @Override
    public void configure() throws Exception {
        // Intercept all route steps to set a start time (once per route execution)
        intercept()
                .process(exchange -> {
                    if (exchange.getProperty("startTime") == null) {
                        exchange.setProperty("startTime", System.currentTimeMillis());
                    }
                });

        // Intercept route entry points to log route invocation (can be extended with correlation ID, etc.)
        interceptFrom()
                .process(exchange -> {
                    String routeId = exchange.getFromRouteId();
                    getLogger().info("[START] Route '{}' invoked", routeId);
                });

        // Log route execution duration on completion (success or failure)
        onCompletion()
                .process(exchange -> {
                    Long start = exchange.getProperty("startTime", Long.class);
                    String routeId = exchange.getFromRouteId();
                    long duration = (start != null) ? System.currentTimeMillis() - start : -1L;
                    getLogger().info("[COMPLETION] Route '{}' took {} ms", routeId, duration);
                });
    }

    /**
     * Implement this to provide a route-specific logger.
     *
     * @return the logger to use for logging route activity
     */
    protected abstract org.slf4j.Logger getLogger();
}