package com.fund.server.accountservice.dto;

public record NormalizedPhone(
        String countryCode,
        String phoneNumber
) {
}