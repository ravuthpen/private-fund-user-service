package com.fund.server.accountservice.dto;

import com.fund.server.accountservice.domain.emuns.ReferralCode;
import com.fund.server.accountservice.domain.emuns.UserStatus;
import com.fund.server.accountservice.domain.emuns.UserType;

import java.util.UUID;

public record UserAccountResponse(
        UUID userAccountId,
        UUID keycloakUserId,
        String countryCode,
        String phoneNumber,
        ReferralCode referralCode,
        UserStatus userStatus,
        UserType userType
        //Instant createdAt
        //Instant updatedAt
) {
}
