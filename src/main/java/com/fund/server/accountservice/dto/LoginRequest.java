package com.fund.server.accountservice.dto;

import com.fund.server.accountservice.domain.emuns.UserType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record LoginRequest(
        @NotBlank String countryCode,
        @NotBlank String phoneNumber,
        @NotNull UserType userType,
        @NotBlank String pin
) {
}
