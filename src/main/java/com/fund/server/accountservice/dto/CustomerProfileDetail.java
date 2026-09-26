package com.fund.server.accountservice.dto;

import com.fund.server.accountservice.domain.emuns.Gender;
import com.fund.server.accountservice.domain.emuns.ReferralCode;
import com.fund.server.accountservice.domain.emuns.UserStatus;
import com.fund.server.accountservice.domain.emuns.UserType;
import lombok.Builder;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Builder
public record CustomerProfileDetail(
        // --- user_account ---
        UUID userAccountId,
        UUID keycloakUserId,
        String countryCode,
        String phoneNumber,
        ReferralCode referralCode,
        UserStatus userStatus,
        UserType userType,

        // --- customer_profile ---
        UUID profileId,              // <-- MISSING from your output. see below.
        String firstName,
        String lastName,
        Gender gender,
        String nationalityNumber,
        String passportNumber,
        LocalDate dateOfBirth,
        Integer age,
        String email,
        List<String> photoProfile,
        List<String> photoObjectKey,
        Instant createdAt,
        Instant updatedAt,

        // --- customer_address (all nullable) ---
        UUID addressId,
        String line1,
        String provinceCode,
        String districtCode,
        String communeCode,
        String villageCode
) {
    public CustomerProfileDetail {
    }
}

