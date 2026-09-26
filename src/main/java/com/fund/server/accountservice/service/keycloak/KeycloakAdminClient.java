package com.fund.server.accountservice.service.keycloak;


import com.fund.server.accountservice.service.keycloak.dto.KeycloakCreateUserRequest;
import com.fund.server.accountservice.service.keycloak.dto.KeycloakResetPasswordRequest;
import reactor.core.publisher.Mono;

public interface KeycloakAdminClient {

    Mono<String> createUser(KeycloakCreateUserRequest request);

    Mono<Void> resetPassword(KeycloakResetPasswordRequest request);
}