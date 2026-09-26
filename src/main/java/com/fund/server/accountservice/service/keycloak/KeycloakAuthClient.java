package com.fund.server.accountservice.service.keycloak;



import com.fund.server.accountservice.dto.LoginResponse;
import reactor.core.publisher.Mono;

public interface KeycloakAuthClient {

    Mono<LoginResponse> login(String username, String pin);
}
