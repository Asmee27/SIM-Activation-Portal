package com.infy.sim.apigateway.security;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

/**
 * Portal users, from config-server api-gateway.properties:
 * {@code app.auth.users.<username>=<bcrypt hash>}
 */
@ConfigurationProperties("app.auth")
public record AuthProperties(Map<String, String> users) {

    public AuthProperties {
        users = users == null ? Map.of() : Map.copyOf(users);
    }
}
