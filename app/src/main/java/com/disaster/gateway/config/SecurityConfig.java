package com.disaster.gateway.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Spring Security configuration for securing API endpoints using Keycloak JWT tokens.
 * <p>
 * Features:
 * <ul>
 *     <li>Disables CSRF for stateless APIs</li>
 *     <li>Allows anonymous access to public endpoints (e.g., {@code /actuator/health}, {@code /api/public/**})</li>
 *     <li>Protects {@code /api/admin/**} endpoints with role-based access (e.g., {@code ROLE_ADMIN})</li>
 *     <li>Maps Keycloak realm and client roles to Spring Security authorities</li>
 * </ul>
 */
@Configuration
public class SecurityConfig {

    /**
     * Injected Keycloak client ID from application properties.
     * Used to extract client-specific roles from JWT's {@code resource_access}.
     */
    @Value("${keycloak.client-id}")
    private String keycloakClientId;

    /**
     * Admin role name configured via application properties (default is {@code ADMIN}).
     * Used for role-based access control on admin endpoints.
     */
    @Value("${security.roles.admin:ADMIN}")
    private String adminRole;

    /**
     * Defines the security filter chain for the application.
     *
     * @param http the {@link HttpSecurity} object to configure
     * @return a configured {@link SecurityFilterChain}
     * @throws Exception in case of configuration error
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // Disable CSRF for APIs (typically stateless)
                .csrf(AbstractHttpConfigurer::disable)

                // Define access rules
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/actuator/health", "/api/public/**", "/api/api-doc").permitAll()
                        .requestMatchers("/api/admin/**").hasRole(adminRole)
                        .anyRequest().authenticated()
                )

                // Configure JWT-based OAuth2 authentication
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(keycloakJwtConverter()))
                );

        return http.build();
    }

    /**
     * Converts a JWT token into a collection of {@link GrantedAuthority} for Spring Security.
     * <p>
     * Supports:
     * <ul>
     *     <li>{@code realm_access.roles} → mapped to {@code ROLE_*}</li>
     *     <li>{@code resource_access.<client>.roles} → mapped to {@code ROLE_*}</li>
     *     <li>{@code scope} → mapped to {@code SCOPE_*}</li>
     * </ul>
     *
     * @return configured {@link JwtAuthenticationConverter}
     */
    private JwtAuthenticationConverter keycloakJwtConverter() {
        JwtGrantedAuthoritiesConverter defaultScopeConverter = new JwtGrantedAuthoritiesConverter();
        defaultScopeConverter.setAuthorityPrefix("SCOPE_");
        defaultScopeConverter.setAuthoritiesClaimName("scope");

        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter((Jwt jwt) -> {
            Collection<GrantedAuthority> authorities = new ArrayList<>(defaultScopeConverter.convert(jwt));

            // Extract realm roles
            Map<String, Object> realmAccess = jwt.getClaim("realm_access");
            if (realmAccess != null && realmAccess.get("roles") instanceof Collection<?> roles) {
                authorities.addAll(roles.stream()
                        .filter(Objects::nonNull)
                        .map(Object::toString)
                        .map(r -> new SimpleGrantedAuthority("ROLE_" + r))
                        .collect(Collectors.toSet()));
            }

            // Extract client roles from resource_access section for this client
            Map<String, Object> resourceAccess = jwt.getClaim("resource_access");
            if (resourceAccess != null) {
                Object apiAccess = resourceAccess.get(keycloakClientId);
                if (apiAccess instanceof Map) {
                    Object clientRoles = ((Map<?, ?>) apiAccess).get("roles");
                    if (clientRoles instanceof Collection<?>) {
                        authorities.addAll(((Collection<?>) clientRoles).stream()
                                .filter(Objects::nonNull)
                                .map(Object::toString)
                                .map(r -> new SimpleGrantedAuthority("ROLE_" + r))
                                .collect(Collectors.toSet()));
                    }
                }
            }

            return authorities;
        });

        return converter;
    }
}