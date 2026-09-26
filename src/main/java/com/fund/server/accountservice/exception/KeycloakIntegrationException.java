package com.fund.server.accountservice.exception;

import org.springframework.http.HttpStatus;

public class KeycloakIntegrationException extends BusinessException {

    private static final long serialVersionUID = 1L;

    public KeycloakIntegrationException(String message) {
        super("KEYCLOAK_INTEGRATION_ERROR", message, HttpStatus.BAD_GATEWAY);
    }
}