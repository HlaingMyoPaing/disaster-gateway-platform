package com.disaster.api.logging;

import org.apache.camel.Exchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.servlet.http.HttpServletRequest;

/**
 * AccessLogger is a utility class that logs HTTP request access information such as:
 * - HTTP method
 * - URI
 * - IP address (with proxy support)
 * - User-Agent
 * - Referer
 * - Accept-Language
 *
 * It provides methods for logging at INFO, DEBUG, and ERROR levels.
 */
public class AccessLogger {

    private static final Logger logger = LoggerFactory.getLogger(AccessLogger.class);

    private static final String HEADER_REQUEST = "CamelPlatformHttpServletRequest";
    private static final String HEADER_HTTP_REMOTE_ADDRESS = "CamelHttpRemoteAddress";
    private static final String HEADER_X_FORWARDED_FOR = "X-Forwarded-For";
    private static final String HEADER_X_REAL_IP = "X-Real-IP";

    /**
     * Extract the real client IP address, checking headers used by proxies first.
     */
    private static String extractIp(Exchange exchange) {
        String ip = exchange.getIn().getHeader(HEADER_X_REAL_IP, String.class);
        if (ip != null && !ip.isEmpty()) {
            logger.debug("X-Real-IP :{}", ip);
            return ip;
        }

        ip = exchange.getIn().getHeader(HEADER_X_FORWARDED_FOR, String.class);
        if (ip != null && !ip.isEmpty()) {
            logger.debug("X-Forwarded-For :{}", ip);
            return ip.split(",")[0].trim();
        }

        HttpServletRequest request = exchange.getIn().getHeader(HEADER_REQUEST, HttpServletRequest.class);
        if (request == null) {
            request = exchange.getIn().getHeader("CamelHttpServletRequest", HttpServletRequest.class);
        }

        if (request != null) {
            return request.getRemoteAddr();
        }

        ip = exchange.getIn().getHeader(HEADER_HTTP_REMOTE_ADDRESS, String.class);
        return ip != null ? ip : "unknown";
    }

    /**
     * Extract the User-Agent from headers or servlet request.
     */
    private static String extractUserAgent(Exchange exchange) {
        return getHeader(exchange, "User-Agent");
    }

    /**
     * Extract the HTTP method (GET, POST, etc.).
     */
    private static String extractMethod(Exchange exchange) {
        HttpServletRequest request = exchange.getIn().getHeader(HEADER_REQUEST, HttpServletRequest.class);
        if (request != null) {
            return request.getMethod();
        }
        return exchange.getIn().getHeader("CamelHttpMethod", String.class);
    }

    /**
     * Extract the requested URI.
     */
    private static String extractUri(Exchange exchange) {
        HttpServletRequest request = exchange.getIn().getHeader(HEADER_REQUEST, HttpServletRequest.class);
        if (request != null) {
            return request.getRequestURI();
        }
        return exchange.getIn().getHeader("CamelHttpUri", String.class);
    }

    /**
     * Extract the Referer header if available.
     */
    private static String extractReferer(Exchange exchange) {
        return getHeader(exchange, "Referer");
    }

    /**
     * Extract the Accept-Language header (e.g., en-US, ja-JP).
     */
    private static String extractAcceptLanguage(Exchange exchange) {
        return getHeader(exchange, "Accept-Language");
    }

    /**
     * Generic method to retrieve a header from the servlet request or Camel headers.
     */
    private static String getHeader(Exchange exchange, String name) {
        HttpServletRequest request = exchange.getIn().getHeader(HEADER_REQUEST, HttpServletRequest.class);
        if (request != null) {
            String value = request.getHeader(name);
            return value != null ? value : "unknown";
        }
        String value = exchange.getIn().getHeader(name, String.class);
        return value != null ? value : "unknown";
    }

    /**
     * Build the access log message for a given exchange and tag.
     *
     * @param tag      Custom tag to identify the route (e.g., "publicHello")
     * @param exchange Camel exchange containing the HTTP request
     * @return Formatted log message
     */
    private static String buildLogMessage(String tag, Exchange exchange) {
        String method = extractMethod(exchange);
        String uri = extractUri(exchange);
        String ip = extractIp(exchange);
        String userAgent = extractUserAgent(exchange);
        String referer = extractReferer(exchange);
        String acceptLang = extractAcceptLanguage(exchange);

        return String.format("[ACCESS] [%s] %s %s from IP=%s | Agent=%s | Referer=%s | Accept-Language=%s",
                tag, method, uri, ip, userAgent, referer, acceptLang);
    }

    /**
     * Log access at INFO level.
     */
    public static void info(Exchange exchange, String tag) {
        if (logger.isInfoEnabled()) {
            logger.info(buildLogMessage(tag, exchange));
        }
    }

    /**
     * Log access at DEBUG level.
     */
    public static void debug(Exchange exchange, String tag) {
        if (logger.isDebugEnabled()) {
            logger.debug(buildLogMessage(tag, exchange));
        }
    }

    /**
     * Log access at ERROR level with exception stacktrace.
     */
    public static void error(Exchange exchange, String tag, Throwable t) {
        if (logger.isErrorEnabled()) {
            logger.error(buildLogMessage(tag, exchange), t);
        }
    }
}