package com.fund.server.accountservice.dto;

import com.fund.server.accountservice.domain.emuns.Gender;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record UpdateCustomerRequest(
        UUID profileId,
        String firstName,
        String lastName,
        Gender gender,
        String nationalityNumber,
        String passportNumber,
        LocalDate dateOfBirth,
        String email,
        String photo_profile,
        List<String> photo_object_key
       // Instant updatedAt
) {
}
