package com.fund.server.accountservice.service.keycloak.dto;

public record KeycloakResetPasswordRequest(
        String keycloakUserId,
        String password,
        boolean temporary
) {
}