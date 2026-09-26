package com.fund.server.accountservice.service.keycloak;

import com.fund.server.accountservice.config.KeycloakProperties;
import com.fund.server.accountservice.exception.KeycloakIntegrationException;
import com.fund.server.accountservice.service.keycloak.dto.KeycloakCreateUserRequest;
import com.fund.server.accountservice.service.keycloak.dto.KeycloakResetPasswordRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class KeycloakAdminClientImpl implements KeycloakAdminClient {

    private static final String PASSWORD_CREDENTIAL_TYPE = "password";

    private final WebClient keycloakWebClient;
    private final KeycloakProperties properties;

    @Override
    public Mono<String> createUser(KeycloakCreateUserRequest request) {
        return getAdminToken()
                .flatMap(token -> keycloakWebClient.post()
                        .uri("/admin/realms/{realm}/users", properties.realm())
                        .headers(headers -> headers.setBearerAuth(token))
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(buildCreateUserPayload(request))
                        .exchangeToMono(response -> {
                            if (response.statusCode().is2xxSuccessful()) {
                                String location = response.headers().asHttpHeaders().getFirst("Location");
                                return Mono.just(extractUserId(location));
                            }

                            return response.bodyToMono(String.class)
                                    .defaultIfEmpty("Keycloak create user failed")
                                    .flatMap(body -> Mono.error(new KeycloakIntegrationException(body)));
                        }));
    }

    @Override
    public Mono<Void> resetPassword(KeycloakResetPasswordRequest request) {
        return getAdminToken()
                .flatMap(token -> keycloakWebClient.put()
                        .uri(
                                "/admin/realms/{realm}/users/{userId}/reset-password",
                                properties.realm(),
                                request.keycloakUserId()
                        )
                        .headers(headers -> headers.setBearerAuth(token))
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(buildResetPasswordPayload(request))
                        .exchangeToMono(response -> {
                            if (response.statusCode().is2xxSuccessful()) {
                                return Mono.empty();
                            }

                            return response.bodyToMono(String.class)
                                    .defaultIfEmpty("Keycloak reset password failed")
                                    .flatMap(body -> Mono.error(new KeycloakIntegrationException(body)));
                        }));
    }

    private Mono<String> getAdminToken() {
        return keycloakWebClient.post()
                .uri("/realms/{realm}/protocol/openid-connect/token", properties.realm())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromFormData("grant_type", "client_credentials")
                        .with("client_id", properties.admin().clientId())
                        .with("client_secret", properties.admin().clientSecret()))
                .exchangeToMono(response ->
                        response.bodyToMono(Map.class)
                                .flatMap(body -> {
                                    if (response.statusCode().isError()) {
                                        return Mono.error(new KeycloakIntegrationException(
                                                "Cannot get Keycloak admin token: " + body
                                        ));
                                    }

                                    return Mono.just((String) body.get("access_token"));
                                })
                )
                .switchIfEmpty(Mono.error(new KeycloakIntegrationException("Cannot get Keycloak admin token")));
    }

    private Map<String, Object> buildCreateUserPayload(KeycloakCreateUserRequest request) {
        return Map.of(
                "username", request.username(),
                "enabled", true,
                "emailVerified", true,
                "firstName", safeValue(request.firstName()),
                "lastName", safeValue(request.lastName()),
                "email", safeValue(request.email()),
                "requiredActions", List.of(),
                "attributes", toKeycloakAttributes(request.attributes()),
                "credentials", List.of(Map.of(
                        "type", PASSWORD_CREDENTIAL_TYPE,
                        "value", request.password(),
                        "temporary", false
                ))
        );
    }

    private Map<String, Object> buildResetPasswordPayload(KeycloakResetPasswordRequest request) {
        return Map.of(
                "type", PASSWORD_CREDENTIAL_TYPE,
                "value", request.password(),
                "temporary", request.temporary()
        );
    }

    private Map<String, List<String>> toKeycloakAttributes(Map<String, String> attributes) {
        if (attributes == null || attributes.isEmpty()) {
            return Map.of();
        }

        return attributes.entrySet()
                .stream()
                .filter(entry -> entry.getValue() != null)
                .collect(java.util.stream.Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> List.of(entry.getValue())
                ));
    }

    private String safeValue(String value) {
        if (value == null || value.isBlank()) {
            return "";
        }

        return value;
    }

    private String extractUserId(String location) {
        if (location == null || !location.contains("/")) {
            throw new KeycloakIntegrationException("Cannot extract Keycloak user id from response");
        }

        return location.substring(location.lastIndexOf('/') + 1);
    }
}