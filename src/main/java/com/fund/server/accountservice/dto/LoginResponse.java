package com.fund.server.accountservice.dto;

public record LoginResponse(
       // String keycloakId,
        String accessToken,
        String refreshToken,
        Long expiresIn,
        String tokenType
) {
}
