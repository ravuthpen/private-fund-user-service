package com.fund.server.accountservice.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "keycloak")
public record KeycloakProperties(
        String baseUrl,
        String realm,
        Admin admin,
        Client client
) {
    public record Admin(String clientId, String clientSecret) {
    }

    public record Client(String clientId, String clientSecret) {
    }
}
