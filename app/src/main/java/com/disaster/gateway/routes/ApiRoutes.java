package com.disaster.gateway.routes;

import com.disaster.gateway.logging.AccessLogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Defines the REST API routes for the Disaster Gateway application.
 * <p>
 * This route builder extends {@link AbstractRouteBuilder}, which provides:
 * <ul>
 *     <li>Automatic start time tracking using {@code intercept()}</li>
 *     <li>Logging of route start using {@code interceptFrom()}</li>
 *     <li>Logging of route completion time using {@code onCompletion()}</li>
 * </ul>
 *
 * Routes defined:
 * <ul>
 *     <li>GET {@code /api/public/hello} → {@code direct:publicHello}</li>
 *     <li>POST {@code /api/admin/hello} → {@code direct:adminHello}</li>
 * </ul>
 */
@Component
public class ApiRoutes extends AbstractRouteBuilder {

    private static final Logger logger = LoggerFactory.getLogger(ApiRoutes.class);

    /**
     * Provides the logger used by {@link AbstractRouteBuilder} for route lifecycle logging.
     *
     * @return logger instance for this route
     */
    @Override
    protected Logger getLogger() {
        return logger;
    }

    /**
     * Configures the REST endpoints and their route handlers.
     * <p>
     * Also calls {@code super.configure()} to ensure the base intercept and completion logic is registered.
     *
     * Endpoints:
     * <ul>
     *     <li>{@code GET /api/public/hello} returns a static hello message</li>
     *     <li>{@code POST /api/admin/hello} returns a static admin hello message</li>
     * </ul>
     *
     * Both endpoints log access via {@link AccessLogger}.
     */
    @Override
    public void configure() throws Exception {
        super.configure();  // Enables intercept(), interceptFrom(), onCompletion() from base class

        // REST configuration (OpenAPI and HTTP component setup)
        restConfiguration()
                .component("platform-http")
                .contextPath("/")
                .apiContextPath("/api/api-doc")
                .apiProperty("api.title", "Camel API")
                .apiProperty("api.version", "1.0");

        // REST endpoint to route mappings
        rest("/api")
                .get("/public/hello").to("direct:publicHello")
                .post("/admin/hello").to("direct:adminHello");

        // Route: publicHello
        from("direct:publicHello")
                .routeId("/publicHello")
                .process(exchange -> AccessLogger.info(exchange, "publicHello"))
                .setBody().constant("{\"message\":\"Hello from public endpoint\"}")
                .setHeader("Content-Type", constant("application/json"));

        // Route: adminHello
        from("direct:adminHello")
                .routeId("/adminHello")
                .process(exchange -> AccessLogger.info(exchange, "adminHello"))
                .setBody().constant("{\"message\":\"Hello from ADMIN endpoint\"}")
                .setHeader("Content-Type", constant("application/json"));
    }
}