package com.fund.server.accountservice.dto;

import com.fund.server.accountservice.domain.emuns.Gender;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record CustomerProfileResponse(

        UUID profileId,
        String firstName,
        String lastName,
        Gender gender,
        String nationalityNumber,
        String passportNumber,
        LocalDate dateOfBirth,
        Integer age,
        String email,
        List<String> photo_profile,
        List<String> photo_object_key,
        Instant createdAt,
        Instant updatedAt
) {
}
