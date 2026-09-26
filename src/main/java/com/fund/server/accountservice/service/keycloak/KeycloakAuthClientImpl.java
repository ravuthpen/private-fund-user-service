package com.fund.server.accountservice.service.keycloak;

import com.fund.server.accountservice.config.KeycloakProperties;
import com.fund.server.accountservice.dto.LoginResponse;
import com.fund.server.accountservice.exception.AccountNotFullySetUpException;
import com.fund.server.accountservice.exception.InvalidCredentialException;
import com.fund.server.accountservice.exception.KeycloakIntegrationException;
import com.fund.server.accountservice.util.LogMasker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import tools.jackson.databind.ObjectMapper;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class KeycloakAuthClientImpl implements KeycloakAuthClient {

    private final WebClient keycloakWebClient;
    private final KeycloakProperties properties;
    private final ObjectMapper objectMapper;

    @Override
    public Mono<LoginResponse> login(String username, String pin) {
        log.info("Calling Keycloak login. username={}", LogMasker.maskPhone(username));

        return keycloakWebClient.post()
                .uri("/realms/{realm}/protocol/openid-connect/token", properties.realm())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromFormData("grant_type", "password")
                        .with("client_id", properties.client().clientId())
                        .with("client_secret", properties.client().clientSecret())
                        .with("username", username)
                        .with("password", pin))
                .exchangeToMono(response ->
                        response.bodyToMono(String.class)
                                .defaultIfEmpty("")
                                .flatMap(body -> handleLoginResponse(
                                        response.statusCode().isError(),
                                        body
                                        )
                                )
                );
    }

    private Mono<LoginResponse> handleLoginResponse(boolean hasError, String body){
        if(!hasError){
            return  toLoginResponse(body);
        }
        log.warn("Keycloak login failed. Body={}", body);

        if(body.contains("Account is not fully set up")){
            return Mono.error(new AccountNotFullySetUpException());
        }
        if(body.contains("invalid_grant")){
            return Mono.error(new InvalidCredentialException());
        }
        return Mono.error(new KeycloakIntegrationException(
                "Unable to login at the moment. Please try again later."
        ));
    }

    /*private Mono<LoginResponse> handleLoginResponse(boolean hasError, String body) {
        if (hasError) {
            log.error("Keycloak login failed. Body: {}", body);

            return Mono.error(new IllegalStateException(
                    "Keycloak login failed: " + body
            ));
        }

        return toLoginResponse(body);
    }*/

    private Mono<LoginResponse> toLoginResponse(String body) {
        try {
            Map<?, ?> response = objectMapper.readValue(body, Map.class);

            return Mono.just(new LoginResponse(
                    getString(response, "access_token"),
                    getString(response, "refresh_token"),
                    getLong(response, "expires_in"),
                    getString(response, "token_type")
            ));
        } catch (Exception ex) {
            return Mono.error(new IllegalStateException(
                    "Failed to parse Keycloak login response: " + body,
                    ex
            ));
        }
    }

    private String getString(Map<?, ?> response, String key) {
        Object value = response.get(key);

        if (value == null) {
            return null;
        }

        return value.toString();
    }

    private Long getLong(Map<?, ?> response, String key) {
        Object value = response.get(key);

        if (value instanceof Number number) {
            return number.longValue();
        }

        if (value != null) {
            return Long.parseLong(value.toString());
        }

        return null;
    }
}